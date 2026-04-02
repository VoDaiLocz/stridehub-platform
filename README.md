# StrideHub

StrideHub is a production-style multi-vendor footwear marketplace built as a modular monolith.

## Implemented Foundation

The repository now includes the `Program 0` engineering baseline:

- profile-based configuration with `local` and `test` environments
- PostgreSQL and Redis local infrastructure through Docker Compose
- baseline Flyway schema for identity, catalog, commerce flow, seller, audit, and outbox tables
- stateless security skeleton with JWT resource-server wiring
- shared API envelope, exception contract, and correlation ID propagation
- runtime OpenAPI endpoint at `/v3/api-docs`
- smoke, security, and migration verification tests using the `test` profile
- GitHub Actions build pipeline for compile and test verification

## Quick Start

### Prerequisites

- Java 21
- Docker Desktop or Docker Engine for local infrastructure

### Local Run

1. Copy `.env.example` to `.env` if you want to override defaults.
2. Start local infrastructure:

   ```powershell
   docker compose up -d
   ```

3. Start the application with the local profile:

   ```powershell
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. Verify the baseline endpoints:

   - `GET http://localhost:8080/actuator/health`
   - `GET http://localhost:8080/v3/api-docs`

### Test and Compile

```powershell
.\mvnw.cmd test
.\mvnw.cmd -q -DskipTests compile
```

## Documentation Status

This repository contains both:

- implemented foundation docs for the current codebase baseline
- forward-looking product, architecture, and execution plans for later phases

## Documentation

- [Documentation Index](./docs/README.md)

## Stack Direction

- Java 21
- Spring Boot 4
- PostgreSQL
- Redis
- Flyway
- JWT-based authentication
- OpenAPI-first REST contracts
