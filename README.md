🚦 Radar Patrol System — Forked & Extended
📌 Project Note
This repository was forked because the topic is highly interesting. Our goal is to make the system versatile compared to a pure Spring Boot 1:1 setup, and to combine Java and Scala technologies in a single distributed architecture.

🗄️ Database
Migrated from the original setup to MariaDB for persistence.

Hibernate/JPA is used across services for ORM, with schema tailored to radar, vehicle, registration, and infraction domains.

🧩 Service Architecture
1. Service Registry
Still implemented with Spring Boot.

Acts as the central discovery mechanism (Eureka).

Other services register here using different client approaches.

2. Radar Service
Implemented in Spring MVC 7 (no Boot, no Spring Data JPA).

Uses MVC boilerplate + Hibernate JPA for persistence.

Responsible for radar lifecycle (create, update, delete, availability toggle).

Provides patrol simulation logic with concurrency safeguards.

3. Auth Service
Built with Spring MVC 7 + Hibernate 7 + Security 7.

Protects endpoints across the system.

Exposes a public key endpoint that other services consume to validate JWT tokens.

Ensures secure distributed communication.

4. API Gateway
Implemented in Scalatra (Scala).

Routes external requests to appropriate backend services.

Uses Apache HTTP client for Eureka integration.

Provides a lightweight, flexible gateway layer.

5. Infraction Service
Implemented in Play Framework (Scala) with Hibernate.

Handles storage and retrieval of traffic infractions.

Consumes events from NATS broker to persist violations.

6. Registration Service
Implemented in Play Framework (Java) with Hibernate.

Manages vehicle registration data.

Provides APIs for radar and patrol services to retrieve vehicle information.

🔗 Service Communication
All services communicate via NATS broker (event-driven messaging).

For service registry (Eureka), different clients are used depending on the stack:

Spring services → RestTemplate or WebService client.

Scala services (Scalatra/Play) → Apache HTTP client.

This hybrid approach demonstrates versatility across ecosystems while maintaining interoperability.

⚡ Key Highlights
Hybrid stack: Java (Spring MVC, Play) + Scala (Scalatra, Play).

Security-first: Auth service with JWT + public key distribution.

Event-driven: NATS broker ensures loose coupling and scalability.

Database consistency: MariaDB with Hibernate ORM across services.

Concurrency safeguards: Patrol logic prevents multiple activations and enforces cooldowns.

📈 Next Steps
Frontend Development

Build a UI to visualize radars, patrols, infractions, and vehicle registrations.

Provide dashboards for monitoring and administration.

Containerization

Package services into Docker containers.

Ensure consistent environments across Java and Scala stacks.

Orchestration

Deploy and manage services using HashiCorp Nomad.

Enable scaling, resilience, and service scheduling in production.

🎯 Educational Value
This forked project serves as a showcase for mentees and collaborators:

Demonstrates how to mix Spring MVC 7, Play, and Scalatra in one ecosystem.

Shows service boundary enforcement and security integration.

Provides a real-world example of polyglot microservices communicating via a broker.

Acts as a foundation for extending into frontend and orchestration layers.