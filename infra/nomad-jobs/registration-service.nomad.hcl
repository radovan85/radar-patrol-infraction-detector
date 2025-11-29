job "registration-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "registration" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 9002
      }
    }

    task "registration" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/radar-patrol/registration-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        DB_URL      = "jdbc:mariadb://localhost:3307/radar-db"
        DB_USERNAME = "root"
        DB_PASSWORD = "1111"
        PLAY_PORT = "9002"
        PLAY_SECRET = ";<q6UYBa=:8IOG`Y^BZfpxlGodEK1yNi^LyH]8limaDllfP1nDXjG59@F_;d?m?`"
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
        cpu    = 700
        memory = 768
      }

      service {
        name     = "registration-service"
        port     = "http"
        tags     = ["registration", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "registration-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}