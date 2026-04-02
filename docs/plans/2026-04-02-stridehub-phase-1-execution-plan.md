# StrideHub Phase 1 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a production-style, Phase 1 backend foundation for StrideHub that supports authentication, catalog, variant inventory, cart, checkout, payment callback handling, order confirmation, and seller approval in a modular monolith.

**Architecture:** The implementation uses a modular monolith with explicit package boundaries for identity, catalog, inventory, cart, checkout, payment, orders, sellers, admin, and shared infrastructure. PostgreSQL is the system of record, Redis is used only for ephemeral and performance-sensitive concerns, and payment is integrated behind a provider abstraction with webhook-driven confirmation.

**Tech Stack:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Redis, Bean Validation, Actuator, OpenAPI, JUnit 5, Spring Boot Test, Testcontainers.

**Repository Layout Note:** The repository now uses `backend/` for the Spring Boot application and `frontend/` for the buyer web workspace. Backend file paths below should be read relative to `backend/`.

**Current Baseline Status:** The current `main` branch already includes Program 0 as historical tasks `0.1` through `0.6`. When continuing from the repository as it exists today, start implementation from **Task 1.1** in this document.

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
- Preferred commit subject format:
  - `task <number>: <description>`
- Examples:
  - `task 0.1: establish backend foundation baseline`
  - `task 1.1: implement authentication and rbac foundation`
  - `task 1.1.1: add user and refresh token entities`

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

## Historical Program 0 Tasks Already Completed

The current repository state already contains the baseline delivered through these historical commits:

1. `task 0.1: establish backend foundation baseline`
2. `task 0.2: add ci baseline and quick-start instructions`
3. `task 0.3: align foundation docs with implementation baseline`
4. `task 0.4: move spring boot application into backend workspace`
5. `task 0.5: add buyer web workspace foundation`
6. `task 0.6: align monorepo docs and ci workflow`

If you need to rebuild Program 0 from scratch, use the full production plan as the canonical reference for the completed baseline history and resulting repository shape.

## Frontend Companion Delivery Rule

Phase 1 is no longer treated as "backend first, frontend later." The execution rule for this repository is:

- backend remains the source of truth for contracts, security, and business rules
- frontend work starts as soon as the corresponding backend milestone is stable enough to integrate
- each backend task below includes a frontend companion scope that should be implemented in the same delivery wave whenever practical
- frontend should not invent contracts ahead of backend, but it also should not wait for all backend tasks to finish before starting buyer-facing work

Use the pattern:

1. implement and verify the backend task
2. wire the matching frontend screen or state flow
3. run an integration checkpoint before moving to the next major task

## Task 1.1: Implement Identity Domain and Security Model

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

**Frontend Companion Work:**

- build `frontend/src` auth screens for register and login
- add buyer header auth state for signed-in vs signed-out modes
- add frontend API client wiring for `/auth/register`, `/auth/login`, `/auth/refresh`, and `/me`
- add route guard placeholder for authenticated pages

**Commit Message:**

`task 1.1: implement authentication and rbac foundation`

## Task 1.2: Implement Catalog Module

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

**Frontend Companion Work:**

- implement buyer home page sections that depend on category, brand, and featured product APIs
- implement product listing page with filter and sort controls
- implement product detail page with variant picker, gallery, and price display
- keep mocked fallback data only where the backend endpoint is still intentionally deferred

**Commit Message:**

`task 1.2: add catalog read APIs for products and variants`

## Task 1.3: Implement Inventory Module

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

**Frontend Companion Work:**

- wire stock and availability badges on product detail and listing cards
- disable impossible variant selections when availability data says the option is unavailable
- show inventory-aware CTA states such as `In stock`, `Low stock`, and `Out of stock`

**Commit Message:**

`task 1.3: implement sku inventory and reservation lifecycle`

## Task 1.4: Implement Cart Module

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

**Frontend Companion Work:**

- implement cart drawer or cart page tied to authenticated buyer state
- add add-to-cart, update quantity, and remove-item flows from product detail and cart UI
- show cart subtotal and validation messages from backend responses

**Commit Message:**

`task 1.4: add buyer cart management`

## Task 1.5: Implement Checkout Session Module

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

**Frontend Companion Work:**

- implement checkout page with address form shell, cart summary, and checkout session creation
- surface backend revalidation errors clearly when price, stock, or product status changes
- display checkout expiry or reservation timeout information in the UI

**Commit Message:**

`task 1.5: implement checkout session creation and validation`

## Task 1.6: Implement Payment Module with Provider Abstraction

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

**Frontend Companion Work:**

- integrate payment initiation from the checkout page
- add pending, failed, and success payment states in the buyer flow
- prepare frontend polling or redirect-handling behavior if the mock provider flow needs it locally

**Commit Message:**

`task 1.6: add payment initiation and webhook processing`

## Task 1.7: Implement Order Module

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

**Frontend Companion Work:**

- implement buyer order confirmation screen after successful payment
- implement order detail and order history views under the buyer account area
- show stable order snapshot data instead of reusing mutable catalog state

**Commit Message:**

`task 1.7: confirm paid checkouts into orders`

## Task 1.8: Implement Seller Application Workflow

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

**Frontend Companion Work:**

- add seller application page in the buyer account area
- add status display for pending, approved, and rejected seller application states
- keep admin approval UI out of scope for buyer web; reserve that for a later internal-facing frontend surface if needed

**Commit Message:**

`task 1.8: implement seller application and approval workflow`

## Task 1.9: Implement Audit and Outbox Foundation

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

**Frontend Companion Work:**

- no major new buyer page is required
- if useful, expose non-sensitive status breadcrumbs in the buyer UI such as payment processing or order confirmation states without leaking audit internals

**Commit Message:**

`task 1.9: add audit logging and outbox persistence foundation`

## Task 1.10: Tighten API Documentation and Error Contract

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

**Frontend Companion Work:**

- remove temporary frontend mocks for any endpoint now implemented for real
- align frontend request and response types with the OpenAPI contract
- verify frontend error rendering matches the shared error envelope

**Commit Message:**

`task 1.10: align openapi contract with implemented phase 1 endpoints`

## Task 1.11: Add Operational Baseline

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

**Frontend Companion Work:**

- confirm frontend local startup, environment variables, and build scripts are documented correctly
- ensure frontend CI steps still reflect actual workspace commands
- clean any temporary UI scaffolding that no longer matches the real API contract

**Commit Message:**

`task 1.11: add ci and operational delivery baseline`

## Task 1.12: Final Phase 1 Verification and Cleanup

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
7. Run buyer-web smoke flow against the completed Phase 1 backend:
   - register or login
   - browse products
   - add variant to cart
   - create checkout session
   - complete mock payment path
   - view order confirmation or order detail

**Verification:**

```powershell
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
.\backend\mvnw.cmd spring-boot:run
pnpm --dir frontend lint
pnpm --dir frontend test --run
pnpm --dir frontend build
```

**Commit Message:**

`task 1.12: finalize and verify phase 1 marketplace backend`

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
- buyer frontend integrates with the completed Phase 1 backend milestones without relying on stale mocks

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

Starting from the current repository baseline, apply commits in roughly this order:

1. `task 1.1: implement authentication and rbac foundation`
2. `task 1.2: add catalog read APIs for products and variants`
3. `task 1.3: implement sku inventory and reservation lifecycle`
4. `task 1.4: add buyer cart management`
5. `task 1.5: implement checkout session creation and validation`
6. `task 1.6: add payment initiation and webhook processing`
7. `task 1.7: confirm paid checkouts into orders`
8. `task 1.8: implement seller application and approval workflow`
9. `task 1.9: add audit logging and outbox persistence foundation`
10. `task 1.10: align openapi contract with implemented phase 1 endpoints`
11. `task 1.11: add ci and operational delivery baseline`
12. `task 1.12: finalize and verify phase 1 marketplace backend`


