terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = ">= 3.0.2"
    }
    nomad = {
      source  = "hashicorp/nomad"
      version = ">= 1.5.0"
    }
  }
}


provider "docker" {
  host = "unix:///var/run/docker.sock"
}

provider "nomad" {
  address = "http://127.0.0.1:4646"
}

resource "docker_image" "consul" {
  name = var.consul_image
}

resource "docker_container" "consul" {
  name         = var.consul_container_name
  image        = docker_image.consul.name   # <-- promenjeno
  network_mode = "host"

  command = [
    "agent", "-dev", "-client=0.0.0.0"
  ]

  restart = "unless-stopped"
}

# === Eureka server job ===
resource "nomad_job" "eureka_server" {
  jobspec = file("${path.module}/nomad-jobs/eureka.nomad.hcl")
}

resource "null_resource" "eureka_ready" {
  depends_on = [nomad_job.eureka_server]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8761/actuator/health; do sleep 5; done'"
  }
}


# === API Gateway job ===
resource "nomad_job" "api_gateway" {
  jobspec    = file("${path.module}/nomad-jobs/api-gateway.nomad.hcl")
  depends_on = [null_resource.eureka_ready]
}

resource "null_resource" "api_gateway_ready" {
  depends_on = [nomad_job.api_gateway]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8080/api/health; do sleep 10; done'"
  }
}


# === NATS job ===
resource "nomad_job" "nats_server" {
  jobspec = file("${path.module}/nomad-jobs/nats.nomad.hcl")
}


resource "null_resource" "nats_ready" {
  depends_on = [nomad_job.nats_server]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf --max-time 5 http://127.0.0.1:8222/; do sleep 10; done'"
  }
}


# === MariaDB job ===
resource "nomad_job" "mariadb" {
  jobspec = file("${path.module}/nomad-jobs/mariadb.nomad.hcl")
}

resource "null_resource" "mariadb_ready" {
  depends_on = [nomad_job.mariadb]

  provisioner "local-exec" {
    command = "bash -c 'while ! nc -z 127.0.0.1 3307; do sleep 10; done'"
  }
}


# === Prometheus job ===
resource "nomad_job" "prometheus" {
  jobspec    = file("${path.module}/nomad-jobs/prometheus.nomad.hcl")
  depends_on = [null_resource.api_gateway_ready]
}

# === Grafana job ===
resource "nomad_job" "grafana" {
  jobspec    = file("${path.module}/nomad-jobs/grafana.nomad.hcl")
  depends_on = [null_resource.api_gateway_ready]
}


# === Auth service job ===
resource "nomad_job" "auth_service" {
  jobspec    = file("${path.module}/nomad-jobs/auth-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.mariadb_ready,
    null_resource.nats_ready
  ]
}

resource "null_resource" "auth_service_ready" {
  depends_on = [nomad_job.auth_service]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8081/api/health; do sleep 10; done'"
  }
}


# === registration service job ===
resource "nomad_job" "registration_service" {
  jobspec    = file("${path.module}/nomad-jobs/registration-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.mariadb_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

resource "null_resource" "registration_service_ready" {
  depends_on = [nomad_job.registration_service]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:9002/api/health; do sleep 10; done'"
  }
}


# === infraction service job ===
resource "nomad_job" "infraction_service" {
  jobspec    = file("${path.module}/nomad-jobs/infraction-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.mariadb_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

resource "null_resource" "infraction_service_ready" {
  depends_on = [nomad_job.infraction_service]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:9001/api/health; do sleep 10; done'"
  }
}


# === radar service job ===
resource "nomad_job" "radar_service" {
  jobspec    = file("${path.module}/nomad-jobs/radar-service.nomad.hcl")
  depends_on = [
  	null_resource.eureka_ready,
  	null_resource.mariadb_ready,
  	null_resource.nats_ready,
  	null_resource.auth_service_ready,
  	null_resource.registration_service_ready,
  	null_resource.infraction_service_ready
  ]
  
}


# === Angular Template job ===
resource "nomad_job" "angular_template" {
  jobspec    = file("${path.module}/nomad-jobs/angular-template.nomad.hcl")
  depends_on = [
    null_resource.api_gateway_ready,
    null_resource.auth_service_ready
  ]
}



