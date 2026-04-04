# StrideHub

StrideHub is a production-style multi-vendor footwear marketplace organized as a clean monorepo.

## Repository Layout

- `backend/` Spring Boot commerce API
- `frontend/` buyer-facing web workspace
- `docs/` engineering plans, architecture, ADRs, runbooks, and audits
- `.github/` CI workflows
- `docker-compose.yml` shared local infrastructure for backend dependencies

## Implemented Baseline

The repository now includes:

- a backend Program 0 foundation with config profiles, security baseline, Flyway schema, tests, and CI coverage
- a backend Phase 1 commerce core with identity, catalog, inventory, cart, checkout, payment, order, seller-application, and audit/outbox baseline modules
- a frontend buyer Phase 1 companion baseline with auth screens, collection browsing, product detail, checkout shell, and buyer order pages
- monorepo root orchestration for docs, compose, workspace dependencies, and delivery flow

## Quick Start

### Prerequisites

- Java 21
- Docker Desktop or Docker Engine for local infrastructure
- Node.js 22
- pnpm 10

### Backend

1. Copy root `.env.example` to `.env` if you want to override infrastructure defaults.
2. Start local infrastructure:

   ```powershell
   docker compose up -d
   ```

3. Start the backend:

   ```powershell
   .\backend\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

   The local profile auto-loads a small catalog and inventory seed so buyer catalog pages have live data on first boot.

4. Verify backend endpoints:

- `GET http://localhost:8080/actuator/health`
- `GET http://localhost:8080/v3/api-docs`
- `GET http://localhost:8080/api/v1/system/ping` with a valid bearer token

### Frontend

1. Copy [frontend/.env.example](./frontend/.env.example) to `frontend/.env` if you want to override the API base URL.
2. Install workspace dependencies:

   ```powershell
   pnpm install
   ```

3. Start the buyer web:

   ```powershell
   pnpm --dir frontend dev
   ```

4. Phase 1 buyer routes available in the frontend:

- `/`
- `/collections/women`
- `/collections/men`
- `/products/{slug}`
- `/auth/login`
- `/auth/register`
- `/checkout`
- `/account/orders`

### Verification

```powershell
.\backend\mvnw.cmd -q verify
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```

## Documentation Status

This repository contains both:

- implemented baseline docs for the current backend/frontend baseline
- forward-looking product, architecture, UI, and execution plans for later phases

## Documentation

- [Documentation Index](./docs/README.md)
- [Master Task Checklist](./TASK-CHECKLIST.md)
- [Execution Checklist and Progress Tracker](./PROGRESS.md)
- [GitHub Engineering Workflow](./docs/github-engineering-workflow.md)
- [TDD Engineering Workflow](./docs/tdd-engineering-workflow.md)
- [Backend Workspace Guide](./backend/README.md)
- [Frontend Workspace Guide](./frontend/README.md)

## Stack Direction

- Java 21
- Spring Boot 4
- React 19
- Vite 8
- PostgreSQL
- Redis
- Flyway
- JWT-based authentication
- OpenAPI-first REST contracts
