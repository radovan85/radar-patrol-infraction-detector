job "mariadb-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "db" {
    count = 1

    # 🛜 Host networking: izbegavamo CNI/iptables komplikacije u WSL-u
    network {
      mode = "host"
      port "db" {
        static = 3307  # MariaDB port
      }
    }

    volume "mariadata" {
      type      = "host"
      source    = "mariadata"
      read_only = false
    }

    task "mariadb-service" {
      driver = "docker"

      config {
        image        = "mariadb:11"
        ports        = ["db"]
        network_mode = "host"
        args = ["--bind-address=0.0.0.0", "--port=3307"]
      }

      env {
        MARIADB_DATABASE      = "radar-db"
        MARIADB_USER          = "root"
        MARIADB_PASSWORD      = "1111"
        MARIADB_ROOT_PASSWORD = "1111"
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

      volume_mount {
        volume      = "mariadata"
        destination = "/var/lib/mysql"
      }

      resources {
        cpu    = 700
        memory = 768
      }

      service {
        name     = "mariadb-service"
        port     = "db"
        tags     = ["sql", "database", "mariadb"]
        provider = "nomad"

        check {
          name     = "mariadb-tcp"
          type     = "tcp"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}