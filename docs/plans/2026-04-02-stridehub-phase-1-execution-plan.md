# StrideHub Phase 1 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a production-style, Phase 1 backend foundation for StrideHub that supports authentication, catalog, variant inventory, cart, checkout, payment callback handling, order confirmation, and seller approval in a modular monolith.

**Architecture:** The implementation uses a modular monolith with explicit package boundaries for identity, catalog, inventory, cart, checkout, payment, orders, sellers, admin, and shared infrastructure. PostgreSQL is the system of record, Redis is used only for ephemeral and performance-sensitive concerns, and payment is integrated behind a provider abstraction with webhook-driven confirmation.

**Tech Stack:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Redis, Bean Validation, Actuator, OpenAPI, JUnit 5, Spring Boot Test, Testcontainers.

**Repository Layout Note:** The repository now uses `backend/` for the Spring Boot application and `frontend/` for the buyer web workspace. Backend file paths below should be read relative to `backend/`.

**Current Baseline Status:** The current `main` branch already includes the project baseline, shared platform layer, database migrations, CI baseline, and monorepo split. When continuing from the repository as it exists today, start implementation from **Task 4** in this document. Tasks 1-3 remain here for traceability and for fresh rebuild scenarios.

---

## Delivery Rules

### Working Rules

- Do not implement Phase 2+ features in this plan.
- Keep module boundaries strict even inside one codebase.
- Do not expose entities directly to controllers.
- Every state transition that touches money, stock, or authorization must have tests.
- Every task below should produce a focused commit.

### Baseline Package Structure

All production code should live under:

- `backend/src/main/java/com/stridehub/common`
- `backend/src/main/java/com/stridehub/config`
- `backend/src/main/java/com/stridehub/identity`
- `backend/src/main/java/com/stridehub/catalog`
- `backend/src/main/java/com/stridehub/inventory`
- `backend/src/main/java/com/stridehub/cart`
- `backend/src/main/java/com/stridehub/checkout`
- `backend/src/main/java/com/stridehub/payment`
- `backend/src/main/java/com/stridehub/order`
- `backend/src/main/java/com/stridehub/seller`
- `backend/src/main/java/com/stridehub/admin`
- `backend/src/main/java/com/stridehub/audit`

Each module should follow this internal structure where applicable:

- `domain`
- `application`
- `infrastructure`
- `web`

### Branch and Commit Strategy

- Keep working on `main` only if this remains a solo local repo.
- Preferred commit format:
  - `chore: ...`
  - `feat: ...`
  - `test: ...`
  - `docs: ...`
  - `refactor: ...`

### Verification Commands

Use these commands repeatedly through the plan:

```powershell
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
.\backend\mvnw.cmd spring-boot:run
```

If Docker is available later:

```powershell
docker compose up -d
.\backend\mvnw.cmd test
```

## Recommended Directory Outputs

By the end of this plan, the repo should contain at minimum:

- `backend/src/main/java/com/stridehub/...`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-local.yml`
- `backend/src/main/resources/db/migration/...`
- `backend/src/test/java/com/stridehub/...`
- `frontend/src/...`
- `docker-compose.yml`
- `.env.example`

## Task 1: Normalize Project Baseline

**Objective:** Turn the starter project into a controllable application baseline suitable for modular work.

**Files:**

- Modify: `backend/pom.xml`
- Modify: `backend/src/main/resources/application.properties`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/application-local.yml`
- Create: `backend/src/main/resources/application-test.yml`
- Create: `.env.example`
- Create: `docker-compose.yml`

**Implementation Steps:**

1. Replace `application.properties` with profile-based YAML configuration.
2. Add dependencies for OpenAPI and any test libraries needed for the first phase.
3. Add explicit app config keys for JWT, payment provider mock settings, and reservation TTL.
4. Add Docker Compose services for PostgreSQL and Redis.
5. Add `.env.example` documenting required environment variables.

**Verification:**

- `.\backend\mvnw.cmd -q -DskipTests compile`
- Confirm application config loads without placeholder resolution failures.

**Commit Message:**

`chore: establish application configuration baseline`

## Task 2: Create Shared Platform Layer

**Objective:** Establish shared conventions before building business modules.

**Files:**

- Create: `backend/src/main/java/com/stridehub/common/api/ApiErrorResponse.java`
- Create: `backend/src/main/java/com/stridehub/common/api/ApiResponse.java`
- Create: `backend/src/main/java/com/stridehub/common/exception/GlobalExceptionHandler.java`
- Create: `backend/src/main/java/com/stridehub/common/exception/BusinessException.java`
- Create: `backend/src/main/java/com/stridehub/common/exception/NotFoundException.java`
- Create: `backend/src/main/java/com/stridehub/common/exception/ConflictException.java`
- Create: `backend/src/main/java/com/stridehub/common/time/TimeProvider.java`
- Create: `backend/src/main/java/com/stridehub/common/time/SystemTimeProvider.java`
- Create: `backend/src/main/java/com/stridehub/common/id/CorrelationIdFilter.java`

**Implementation Steps:**

1. Define a consistent JSON error envelope.
2. Add global exception handling for validation, security, and business exceptions.
3. Introduce a `TimeProvider` abstraction to make expiry logic testable.
4. Add correlation ID handling at the web layer.

**Verification:**

- Add controller-slice tests for validation and error envelope behavior.
- `.\backend\mvnw.cmd test`

**Commit Message:**

`feat: add shared api and exception conventions`

## Task 3: Create Database Foundation and Flyway Baseline

**Objective:** Establish a durable relational schema for the first delivery slice.

**Files:**

- Create: `backend/src/main/resources/db/migration/V1__baseline_identity_and_catalog.sql`
- Create: `backend/src/main/resources/db/migration/V2__commerce_core.sql`
- Create: `backend/src/main/resources/db/migration/V3__seller_and_audit.sql`
- Create: `backend/src/test/java/com/stridehub/integration/FlywayMigrationTest.java`

**Schema Scope:**

- identity: `users`, `roles`, `user_roles`, `refresh_tokens`, `addresses`
- seller: `seller_profiles`, `seller_applications`
- catalog: `categories`, `brands`, `products`, `product_variants`, `product_images`
- inventory: `inventory_items`, `inventory_reservations`
- cart: `carts`, `cart_items`
- checkout/payment/order: `checkout_sessions`, `payments`, `payment_attempts`, `orders`, `order_items`
- audit: `audit_logs`, `outbox_events`

**Implementation Steps:**

1. Write normalized schema with UUID primary keys.
2. Add unique constraints for email, SKU, provider references.
3. Add indexes for reservation expiry, order lookup, audit correlation, and product slug.
4. Add migration test that boots context against a real PostgreSQL-compatible database when available.

**Verification:**

- `.\backend\mvnw.cmd test`
- Inspect generated tables in local database.

**Commit Message:**

`feat: add flyway baseline schema for phase 1`

## Task 4: Implement Identity Domain and Security Model

**Objective:** Deliver authentication and authorization foundations required by every later module.

**Files:**

- Create: `backend/src/main/java/com/stridehub/identity/domain/...`
- Create: `backend/src/main/java/com/stridehub/identity/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/identity/application/...`
- Create: `backend/src/main/java/com/stridehub/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/stridehub/config/JwtProperties.java`
- Create: `backend/src/main/java/com/stridehub/identity/web/AuthController.java`
- Create: `backend/src/main/java/com/stridehub/identity/web/MeController.java`
- Create: `backend/src/test/java/com/stridehub/identity/...`

**Required Features:**

- register
- login
- refresh token rotation
- current user endpoint
- role mapping for `BUYER`, `SELLER`, `ADMIN`
- password hashing using Argon2

**Implementation Steps:**

1. Implement `User`, `Role`, `RefreshToken`, and `Address` entities.
2. Seed base roles through migration or startup seed.
3. Implement JWT generation and validation.
4. Implement login with password verification.
5. Implement refresh token rotation and revocation.
6. Expose `/auth/register`, `/auth/login`, `/auth/refresh`, `/me`.

**Verification:**

- unit tests for token service
- integration tests for register/login/refresh
- authorization tests for protected endpoints

**Commit Message:**

`feat: implement authentication and rbac foundation`

## Task 5: Implement Catalog Module

**Objective:** Build the buyer-facing product data model and read APIs.

**Files:**

- Create: `backend/src/main/java/com/stridehub/catalog/domain/...`
- Create: `backend/src/main/java/com/stridehub/catalog/application/...`
- Create: `backend/src/main/java/com/stridehub/catalog/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/catalog/web/CatalogController.java`
- Create: `backend/src/test/java/com/stridehub/catalog/...`

**Required Features:**

- category and brand lookup
- product listing
- product detail by slug
- variant listing inside product detail
- product status filtering to only expose `ACTIVE`

**Implementation Steps:**

1. Implement `Category`, `Brand`, `Product`, `ProductVariant`, `ProductImage`.
2. Create repository queries for listing and detail.
3. Support filter inputs for category, brand, size, and color.
4. Build response DTOs distinct from entities.
5. Add seed data strategy or test fixtures for catalog endpoints.

**Verification:**

- API tests for listing and detail
- tests confirming non-active products are not exposed

**Commit Message:**

`feat: add catalog read APIs for products and variants`

## Task 6: Implement Inventory Module

**Objective:** Create stock control that is safe enough for checkout orchestration.

**Files:**

- Create: `backend/src/main/java/com/stridehub/inventory/domain/...`
- Create: `backend/src/main/java/com/stridehub/inventory/application/...`
- Create: `backend/src/main/java/com/stridehub/inventory/infrastructure/...`
- Create: `backend/src/test/java/com/stridehub/inventory/...`

**Required Features:**

- inventory by variant SKU
- reservation create
- reservation commit
- reservation release
- reservation expiry support

**Implementation Steps:**

1. Model `InventoryItem` and `InventoryReservation`.
2. Implement optimistic locking or equivalent concurrency control on stock updates.
3. Create reservation service taking variant IDs and quantities.
4. Ensure reservation creation fails when available stock is insufficient.
5. Implement commit and release semantics.
6. Add expiry support using timestamp fields and scheduled release hook placeholder.

**Verification:**

- unit tests for reservation rules
- concurrency integration test for low-stock reservation race

**Commit Message:**

`feat: implement sku inventory and reservation lifecycle`

## Task 7: Implement Cart Module

**Objective:** Give buyers a persistent cart that references real variants.

**Files:**

- Create: `backend/src/main/java/com/stridehub/cart/domain/...`
- Create: `backend/src/main/java/com/stridehub/cart/application/...`
- Create: `backend/src/main/java/com/stridehub/cart/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/cart/web/CartController.java`
- Create: `backend/src/test/java/com/stridehub/cart/...`

**Required Features:**

- get current cart
- add item
- update item quantity
- remove item
- subtotal calculation

**Implementation Steps:**

1. Create `Cart` and `CartItem` entities.
2. Ensure each buyer has at most one active cart.
3. Validate variant existence and positive quantity.
4. Return cart with variant SKU and pricing snapshot for display.
5. Do not treat cart price as authoritative for checkout.

**Verification:**

- controller tests for add/update/remove
- integration test for cart ownership isolation

**Commit Message:**

`feat: add buyer cart management`

## Task 8: Implement Checkout Session Module

**Objective:** Transform a validated cart into a payment-ready checkout session.

**Files:**

- Create: `backend/src/main/java/com/stridehub/checkout/domain/...`
- Create: `backend/src/main/java/com/stridehub/checkout/application/...`
- Create: `backend/src/main/java/com/stridehub/checkout/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/checkout/web/CheckoutController.java`
- Create: `backend/src/test/java/com/stridehub/checkout/...`

**Required Features:**

- create checkout session
- revalidate prices and variants
- reserve inventory
- expose reservation expiry timestamp

**Implementation Steps:**

1. Implement `CheckoutSession` entity with lifecycle fields.
2. Load current cart and validate item availability.
3. Recalculate totals from source catalog data.
4. Reserve inventory through the inventory module.
5. Persist checkout session with status and expiry timestamp.

**Verification:**

- test that checkout fails when a variant is inactive or out of stock
- test that checkout creates inventory reservations

**Commit Message:**

`feat: implement checkout session creation and validation`

## Task 9: Implement Payment Module with Provider Abstraction

**Objective:** Integrate payment initiation without coupling business logic to a concrete provider too early.

**Files:**

- Create: `backend/src/main/java/com/stridehub/payment/domain/...`
- Create: `backend/src/main/java/com/stridehub/payment/application/...`
- Create: `backend/src/main/java/com/stridehub/payment/infrastructure/provider/PaymentProvider.java`
- Create: `backend/src/main/java/com/stridehub/payment/infrastructure/provider/MockPaymentProvider.java`
- Create: `backend/src/main/java/com/stridehub/payment/web/PaymentWebhookController.java`
- Create: `backend/src/test/java/com/stridehub/payment/...`

**Required Features:**

- create payment request for a checkout session
- store provider reference
- track payment attempts
- verify webhook signature through provider abstraction
- idempotent webhook event processing

**Implementation Steps:**

1. Model `Payment` and `PaymentAttempt`.
2. Create provider abstraction with methods for initiation and signature verification.
3. Implement mock provider behavior suitable for local development.
4. Store webhook event identifiers or equivalent idempotency markers.
5. Ensure repeated success webhook does not duplicate downstream actions.

**Verification:**

- unit tests for provider abstraction behavior
- integration test for duplicate webhook replay

**Commit Message:**

`feat: add payment initiation and webhook processing`

## Task 10: Implement Order Module

**Objective:** Confirm paid checkouts into orders and commit reserved stock.

**Files:**

- Create: `backend/src/main/java/com/stridehub/order/domain/...`
- Create: `backend/src/main/java/com/stridehub/order/application/...`
- Create: `backend/src/main/java/com/stridehub/order/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/order/web/OrderController.java`
- Create: `backend/src/test/java/com/stridehub/order/...`

**Required Features:**

- create order from confirmed payment
- persist order items
- commit inventory reservations
- expose order detail
- support initial cancellation state rules

**Implementation Steps:**

1. Implement `Order` and `OrderItem`.
2. Orchestrate order confirmation only from verified payment events.
3. Commit matching inventory reservations.
4. Persist a stable order snapshot so later catalog changes do not rewrite history.
5. Expose `GET /orders/{id}` and buyer order listing if time permits.

**Verification:**

- integration test from payment success to order creation
- test that repeated webhook does not create a duplicate order

**Commit Message:**

`feat: confirm paid checkouts into orders`

## Task 11: Implement Seller Application Workflow

**Objective:** Add marketplace governance without full seller tooling yet.

**Files:**

- Create: `backend/src/main/java/com/stridehub/seller/domain/...`
- Create: `backend/src/main/java/com/stridehub/seller/application/...`
- Create: `backend/src/main/java/com/stridehub/seller/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/seller/web/SellerApplicationController.java`
- Create: `backend/src/main/java/com/stridehub/admin/web/AdminSellerController.java`
- Create: `backend/src/test/java/com/stridehub/seller/...`

**Required Features:**

- submit seller application
- admin approve seller
- admin reject seller
- reflect role/state changes

**Implementation Steps:**

1. Implement `SellerApplication` and `SellerProfile`.
2. Restrict seller application submission to authenticated users.
3. Restrict approval endpoints to admins.
4. Record approval decision reason.
5. Update role assignment or seller status on approval.

**Verification:**

- tests for buyer cannot call admin endpoints
- tests for approval changing seller state

**Commit Message:**

`feat: implement seller application and approval workflow`

## Task 12: Implement Audit and Outbox Foundation

**Objective:** Add the minimum operational backbone needed for privileged and financial workflows.

**Files:**

- Create: `backend/src/main/java/com/stridehub/audit/domain/...`
- Create: `backend/src/main/java/com/stridehub/audit/application/...`
- Create: `backend/src/main/java/com/stridehub/audit/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/common/events/...`
- Create: `backend/src/test/java/com/stridehub/audit/...`

**Required Features:**

- audit log persistence
- actor and correlation ID capture
- outbox event persistence for major transitions

**Implementation Steps:**

1. Create `AuditLog` and `OutboxEvent` persistence model.
2. Persist audit records for:
   - seller approval
   - payment webhook processing
   - order confirmation
3. Add outbox records for payment confirmed and order created events.
4. Keep publisher implementation minimal for Phase 1 if necessary, but persist the records now.

**Verification:**

- integration tests showing audit rows are written for admin approval and order confirmation

**Commit Message:**

`feat: add audit logging and outbox persistence foundation`

## Task 13: Tighten API Documentation and Error Contract

**Objective:** Ensure the implementation and docs remain synchronized.

**Files:**

- Modify: `docs/api/openapi.yaml`
- Modify: any controller DTO package under `backend/src/main/java/com/stridehub/**/web`
- Create: `backend/src/test/java/com/stridehub/contracts/...`

**Implementation Steps:**

1. Align request and response DTO names with OpenAPI.
2. Ensure error envelope matches the documented structure.
3. Update OpenAPI spec for all implemented endpoints.
4. Add contract tests for the webhook payload shape and core auth/cart/checkout flows.

**Verification:**

- validate YAML syntax
- run API tests against implemented controllers

**Commit Message:**

`docs: align openapi contract with implemented phase 1 endpoints`

## Task 14: Add Operational Baseline

**Objective:** Make the project runnable and reviewable as a professional backend project.

**Files:**

- Modify: `README.md`
- Modify: `docs/test-strategy.md`
- Modify: `docs/release-checklist.md`
- Modify: `docs/runbooks/*`
- Create: `.github/workflows/build.yml`

**Implementation Steps:**

1. Update root README with setup and run instructions.
2. Add a minimal CI workflow for compile and test.
3. Confirm local startup and test instructions reflect the actual implementation.
4. Refine runbooks if implementation details changed from the original design assumptions.

**Verification:**

- `.\backend\mvnw.cmd test`
- review CI YAML for syntax correctness

**Commit Message:**

`chore: add ci and operational delivery baseline`

## Task 15: Final Phase 1 Verification and Cleanup

**Objective:** Prove the phase is coherent, runnable, and reviewable.

**Files:**

- Modify only if needed to fix failing verification

**Checklist:**

1. Run full test suite.
2. Run compile without tests.
3. Start the application locally.
4. Execute a manual smoke flow:
   - register
   - login
   - list products
   - add to cart
   - create checkout session
   - simulate payment success webhook
   - read order detail
5. Confirm audit logs exist for the approval and payment path.
6. Confirm duplicate webhook replay does not duplicate order creation.

**Verification:**

```powershell
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
.\backend\mvnw.cmd spring-boot:run
```

**Commit Message:**

`chore: finalize and verify phase 1 marketplace backend`

## Definition of Done for This Plan

The plan is complete only when all of the following are true:

- authentication works with JWT and refresh rotation
- catalog exposes active products and variants
- cart works for authenticated buyers
- checkout creates inventory reservations
- payment initiation exists behind a provider abstraction
- signed webhook processing is idempotent
- successful payment confirms an order exactly once
- seller application and admin approval work
- audit records exist for privileged and finance-sensitive flows
- OpenAPI, README, and runbooks reflect the implemented behavior

## What This Plan Deliberately Leaves for Later

The following are intentionally not required to declare success for this file:

- real payment gateway credentials
- seller payouts
- multi-warehouse support
- recommendation engine
- advanced search infrastructure
- fraud scoring
- production-grade background worker scaling

## Recommended Commit Sequence

Apply commits in roughly this order:

1. `chore: establish application configuration baseline`
2. `feat: add shared api and exception conventions`
3. `feat: add flyway baseline schema for phase 1`
4. `feat: implement authentication and rbac foundation`
5. `feat: add catalog read APIs for products and variants`
6. `feat: implement sku inventory and reservation lifecycle`
7. `feat: add buyer cart management`
8. `feat: implement checkout session creation and validation`
9. `feat: add payment initiation and webhook processing`
10. `feat: confirm paid checkouts into orders`
11. `feat: implement seller application and approval workflow`
12. `feat: add audit logging and outbox persistence foundation`
13. `docs: align openapi contract with implemented phase 1 endpoints`
14. `chore: add ci and operational delivery baseline`
15. `chore: finalize and verify phase 1 marketplace backend`

