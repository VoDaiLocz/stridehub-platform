# StrideHub Backend

Spring Boot workspace for the StrideHub marketplace platform.

## Current Backend Scope

The implemented backend baseline now covers:

- identity and JWT-based authentication
- catalog discovery for categories, brands, products, and product detail
- SKU inventory with reservation, release, commit, and expiry support
- buyer cart management
- checkout-session creation and revalidation
- payment intent creation with signed webhook processing
- order creation from captured payments
- seller application submission and current-status lookup
- audit log and outbox persistence for key Phase 1 transitions

## Stack

- Java 21
- Spring Boot 4
- PostgreSQL
- Redis
- Flyway
- Spring Security

## Local Development

1. Start infrastructure from the repository root:

   ```powershell
   docker compose up -d
   ```

2. Run the API:

   ```powershell
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. Useful local endpoints:

- `GET /actuator/health`
- `GET /v3/api-docs`
- `GET /api/v1/system/ping` with a bearer token
- `POST /api/v1/webhooks/payment/provider` for mock payment callback testing

## Verification

```powershell
.\mvnw.cmd test
.\mvnw.cmd -q -DskipTests compile
```
