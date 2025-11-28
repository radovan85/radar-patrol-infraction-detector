job "infraction-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "infraction" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 9001
      }
    }

    task "infraction" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/radar-patrol/infraction-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        DB_URL      = "jdbc:mariadb://localhost:3307/radar-db"
        DB_USERNAME = "root"
        DB_PASSWORD = "1111"
        PLAY_PORT = "9001"
        PLAY_SECRET = "wn^AL?B_@ue@tUbrBIpsHFqi]1WQtkL<DDVdumZEtbH?]@ICQzIaNg8u0r8?Ctcz"
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
        name     = "infraction-service"
        port     = "http"
        tags     = ["infraction", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "infraction-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}