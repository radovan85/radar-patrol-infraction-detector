job "angular-template" {
  datacenters = ["dc1"]
  type        = "service"

  group "frontend" {
    count = 1

    network {
      mode = "host"

      port "http" {
        static = 4200
      }
    }

    task "angular" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/radar-patrol/angular-template:main"
        ports        = ["http"]
        network_mode = "host"
      }

      resources {
        cpu    = 500
        memory = 512
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

      service {
        name     = "angular-template"
        port     = "http"
        tags     = ["frontend", "angular", "nginx"]
        provider = "nomad"

        check {
          name          = "angular-health"
          type          = "http"
          path          = "/"
          interval      = "10s"
          timeout       = "2s"
          address_mode  = "host"
        }
      }

      
    }
  }
}