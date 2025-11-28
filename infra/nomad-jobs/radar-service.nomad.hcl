job "radar-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "radar" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 8082
      }
    }

    task "radar" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/radar-patrol/radar-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        DB_URL      = "jdbc:mariadb://localhost:3307/radar-db"
        DB_USERNAME = "root"
        DB_PASSWORD = "1111"
        SPRING_PORT = "8082"
      }
      
      lifecycle {
        hook    = "prestart"
        sidecar = false
      }

      restart {
        attempts = 10
        interval = "5m"
        delay    = "15s"
        mode     = "delay"
      }

      resources {
        cpu    = 500
        memory = 512
      }

      service {
        name     = "RADAR-service"
        port     = "http"
        tags     = ["RADAR", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "RADAR-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}