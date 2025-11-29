🚦 Radar Patrol System — Forked & Extended
📌 Project Note This repository was forked because the topic is highly interesting. The goal is to make the system more versatile compared to a pure Spring Boot 1:1 setup, and to combine Java and Scala technologies in a single distributed architecture.

🗄️ Database
Migrated to MariaDB for persistence.

Hibernate/JPA is used across services for ORM, with schema tailored to radar, vehicle, registration, and infraction domains.

🧩 Service Architecture
Service Registry

Implemented with Spring Boot (Eureka).

Acts as the central discovery mechanism.

Other services register here using different client approaches.

Radar Service

Implemented in Spring MVC 7 (no Boot, no Spring Data JPA).

Uses MVC boilerplate + Hibernate JPA for persistence.

Responsible for radar lifecycle (create, update, delete, availability toggle).

Provides patrol simulation logic with concurrency safeguards.

Auth Service

Built with Spring MVC 7 + Hibernate 7 + Security 7.

Protects endpoints across the system.

Exposes a public key endpoint that other services consume to validate JWT tokens.

Ensures secure distributed communication.

API Gateway

Implemented in Scalatra (Scala).

Routes external requests to appropriate backend services.

Uses Apache HTTP client for Eureka integration.

Provides a lightweight, flexible gateway layer.

Infraction Service

Implemented in Play Framework (Scala) with Hibernate.

Handles storage and retrieval of traffic infractions.

Consumes events from NATS broker to persist violations.

Registration Service

Implemented in Play Framework (Java) with Hibernate.

Manages vehicle registration data.

Provides APIs for radar and patrol services to retrieve vehicle information.

🔗 Service Communication
All services communicate via NATS broker (event-driven messaging).

For service registry (Eureka), different clients are used depending on the stack:

Spring services → RestTemplate or WebService client.

Scala services (Scalatra/Play) → Apache HTTP client.

⚡ Key Highlights
Hybrid stack: Java (Spring MVC, Play) + Scala (Scalatra, Play).

Security-first: Auth service with JWT + public key distribution.

Event-driven: NATS broker ensures loose coupling and scalability.

Database consistency: MariaDB with Hibernate ORM across services.

Concurrency safeguards: Patrol logic prevents multiple activations and enforces cooldowns.

🖥️ Frontend
Implemented in Angular + TypeScript + Foundation.

Provides dashboards for monitoring and administration.

Visualizes radars, patrols, infractions, and vehicle registrations.

⚙️ Orchestration & Infrastructure
HashiCorp Nomad: job files for each service, plus NATS, MariaDB, Prometheus, Grafana.

Terraform: automated provisioning and deployment of the entire stack.

Observability: Prometheus + Grafana integrated for metrics and visualization.

Resilience & scaling: Nomad scheduling ensures services can scale horizontally and recover from failures.

🎯 Educational Value
Demonstrates how to mix Spring MVC 7, Play, and Scalatra in one ecosystem.

Shows service boundary enforcement and security integration.

Provides a real-world example of polyglot microservices communicating via a broker.

Extends into frontend and orchestration layers, making the system production-ready.

Acts as a foundation for mentees and collaborators to learn DevOps practices (Nomad + Terraform + monitoring).


1. Prerequisites
Before running the system, make sure you have the following installed on your machine:

Linux (or WSL2 on Windows, which was used during development)

Docker (for pulling and running public images)

HashiCorp Nomad (for orchestration and scheduling)

Terraform (for infrastructure provisioning)

⚠️ No manual builds (mvn clean install, sbt dist, npm install) are required. All services are already built and published as public Docker images via GitHub Actions.


2. Environment Preparation
Before running Terraform and deploying Nomad jobs, you need to prepare the environment. This ensures that all infrastructure scripts and services have the correct working context and persistent storage.

2.1 Navigate to the infra folder
From the project root, switch into the infra directory:

bash
cd infra
Why this is required:

The infra folder contains all infrastructure‑related configuration: Terraform scripts, Nomad job files, and helper scripts.

Running commands from this folder ensures Terraform and Nomad can correctly locate their configuration files without path errors.

It keeps the workflow clean and reproducible, so mentees or collaborators always know where infrastructure commands should be executed.

2.2 Create persistent volume directories
Next, create host directories that will be mounted as persistent volumes:

bash
sudo mkdir -p /opt/nomad-volumes/pgdata
sudo mkdir -p /opt/nomad-volumes/prometheus
sudo mkdir -p /opt/nomad-volumes/grafana
sudo mkdir -p /opt/nomad-volumes/mariadata

sudo chown 472:472 /opt/nomad-volumes/grafana
Why this is required:

/opt/nomad-volumes/pgdata → stores PostgreSQL (or other DB) data so the database state persists across container restarts.

/opt/nomad-volumes/prometheus → keeps Prometheus metrics data persistent, ensuring historical monitoring data is not lost.

/opt/nomad-volumes/grafana → holds Grafana dashboards and configuration. Ownership is set to user ID 472 because Grafana runs under that UID inside its container.

/opt/nomad-volumes/mariadata → stores MariaDB data files for persistence.

Without these directories, data would be ephemeral and lost whenever Nomad reschedules or restarts jobs.

👉 With Step 2 complete, the environment is properly prepared: you are in the correct working directory (infra), and all persistent storage paths are ready for Nomad and Terraform to use.


3. Configure Prometheus targets
Prometheus needs a configuration file to know which services to scrape for metrics. You’ll create it inside the persistent volume so the Prometheus container can mount and read it on startup.

3.1 Create the configuration file
Open and edit the file:

bash
sudo nano /opt/nomad-volumes/prometheus/prometheus.yml
Paste the following content:

yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'api-gateway'
    metrics_path: '/prometheus'
    static_configs:
      - targets: [ 'api-gateway:8080' ]

  - job_name: 'api-gateway-all'
    metrics_path: '/prometheus/metrics'
    static_configs:
      - targets: [ 'api-gateway:8080' ]

  - job_name: 'auth-service'
    metrics_path: '/prometheus'
    static_configs:
      - targets: [ 'localhost:8081' ]

  - job_name: 'radar-service'
    metrics_path: '/prometheus'
    static_configs:
      - targets: [ 'localhost:8082' ]

  - job_name: 'registration-service'
    metrics_path: '/prometheus'
    static_configs:
      - targets: [ 'localhost:9002' ]

  - job_name: 'infraction-service'
    metrics_path: '/prometheus'
    static_configs:
      - targets: [ 'localhost:9001' ]

  - job_name: 'prometheus'
    static_configs:
      - targets: [ 'localhost:9090' ]



3.2 Why this step is required
Prometheus discovery: Prometheus does not auto-discover targets by default. This file declares which endpoints to scrape and on which paths.

Persistent location: Placing the file under /opt/nomad-volumes/prometheus/ ensures it is available across restarts and can be mounted by the Prometheus job as a volume.

Consistent scrape cadence:

global.scrape_interval sets how often Prometheus pulls metrics.

evaluation_interval determines how often rules are evaluated (handy if you add alerting rules later).



4. 🧭 Starting the Nomad Agent
Once the persistent volumes are created and Prometheus is configured, you can start the Nomad agent using the provided configuration file:

bash
nomad agent -config=./nomad-config/nomad.hcl
🔎 Why this step is required
Nomad orchestration: The agent is the core process that runs Nomad. Without it, jobs cannot be submitted or scheduled.

Server + client mode: The provided configuration (nomad.hcl) launches Nomad in both server and client mode, meaning this single node can accept jobs and also run them. This is ideal for local development and WSL setups.

Consul integration: The configuration integrates with Consul for service discovery, so services can register and discover each other automatically.

Docker driver enabled: The agent is configured to use Docker as its runtime driver, allowing Nomad to schedule and manage Docker containers for each service.

Ready for job submissions: Once the agent is running, you can submit Nomad job files (*.nomad) to deploy services like radar-service, auth-service, api-gateway, and monitoring tools.



5. 🚀 Provision and Deploy with Terraform
Once the Nomad agent is running, the next step is to let Terraform provision and deploy the entire microservice ecosystem.

5.1 Navigate back to the infra folder
From your project root, switch again into the infra directory:

bash
cd infra
Why this is required:

The infra folder contains all Terraform configuration files (*.tf) that describe how Nomad jobs, volumes, and supporting services should be deployed.

Running Terraform commands from this folder ensures it can correctly locate and apply the infrastructure definitions.

5.2 Run Terraform commands
Execute the following commands in sequence:

bash
terraform init
terraform plan
terraform apply
terraform init → initializes the working directory, downloads providers (Nomad, Docker, etc.), and prepares the backend.

terraform plan → shows a preview of what Terraform will create, update, or destroy. This is your chance to verify the changes before applying them.

terraform apply → applies the plan and provisions all resources. You will be asked to confirm before execution.

5.3 What happens after confirmation
Terraform will submit all Nomad job files automatically.

Each microservice (radar-service, auth-service, registration-service, infraction-service, api-gateway) will be deployed as a Nomad job.

Supporting infrastructure (NATS broker, MariaDB, Prometheus, Grafana, and the Angular frontend) will also be started.

Nomad will orchestrate containers, allocate resources, and ensure health checks pass.

Within a few minutes, the entire distributed system will be up and running.

👉 With Step 5 complete, you now have a fully deployed microservice architecture managed by Nomad and provisioned by Terraform. From here, you can access the frontend, API gateway, and monitoring dashboards exactly as described in Step 4.


📬 Contact
For assistance with running the project or to discuss potential future collaboration, please reach out via email:

philip_rivers85@yahoo.com
