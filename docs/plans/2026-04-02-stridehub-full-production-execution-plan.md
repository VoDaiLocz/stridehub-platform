# StrideHub Full Production Execution Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Deliver a production-grade multi-vendor footwear marketplace platform from foundation through operations readiness, including a complete buyer commerce flow, seller onboarding and governance, payment-confirmed ordering, refund and reconciliation support, observability, CI/CD, and operational runbooks.

**Architecture:** The system is implemented as a modular monolith with strict domain boundaries so it can be built by a small team while still reflecting enterprise engineering standards. PostgreSQL is the source of truth, Redis supports ephemeral and performance-sensitive workflows, and all finance- and stock-sensitive operations are built around explicit state transitions, audit trails, idempotency, and webhook-driven confirmation.

**Tech Stack:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Redis, Bean Validation, Actuator, OpenAPI, JUnit 5, Spring Boot Test, Testcontainers, Docker Compose, GitHub Actions, OpenTelemetry-ready instrumentation.

**Repository Layout Note:** The repository now uses a monorepo shape with `backend/` for the Spring Boot application and `frontend/` for the buyer web workspace. Unless explicitly stated otherwise, backend file paths below should be read relative to `backend/`.

**Current Baseline Status:** Program 0 has already been implemented on the current `main` branch, including configuration profiles, shared API conventions, baseline schema migrations, security baseline, CI, and the `backend/` + `frontend/` monorepo structure. When executing from the current repository state, the next actionable task is **Program 1 / Task 1.1**. Program 0 remains documented here as the implementation record and rebuild guide.

---

## 1. Plan Usage Rules

### 1.1 Execution Style

- Treat this file as the primary implementation playbook for the entire product roadmap.
- Execute tasks in order unless the dependency notes explicitly allow parallelization.
- Do not skip verification steps.
- Keep commits task-scoped and reviewable.
- Update project documentation whenever implementation changes a documented contract or behavior.

### 1.2 Implementation Scope Model

This plan is split into five execution programs:

- Program 0: Engineering foundation
- Program 1: Commerce core
- Program 2: Marketplace governance
- Program 3: Financial and operational integrity
- Program 4: Scale and production readiness

### 1.2.1 Backend-to-Frontend Delivery Cadence

- backend remains the contract owner for auth, catalog, checkout, and order logic
- frontend should begin as soon as a backend milestone is verified, not after the entire backend roadmap is complete
- every Program 1 and Program 2 task that materially changes buyer or seller experience should carry a frontend companion scope
- the delivery rhythm is:
  1. implement backend capability
  2. verify backend contract
  3. wire frontend screen, state, or integration against that contract
  4. run an integration checkpoint before advancing
- seller/admin internal UI may lag buyer UI when the backend boundary is still the higher priority

### 1.3 Branch and Commit Policy

- If this remains a single local repo, commits may land on `main`.
- If implementation becomes multi-session, move execution into feature branches or worktrees.
- Keep one commit per task or subtask cluster, never one commit per entire phase.

### 1.4 Commit Message Convention

- Primary commit subject format:
  - `task <number>: <description>`
- Examples:
  - `task 0.1: establish backend foundation baseline`
  - `task 1.1: implement identity and token lifecycle`
  - `task 1.1.1: add user and refresh token entities`
- Use one top-level task number when the commit cleanly maps to a single plan task.
- Use a decimal subtask suffix when a task needs multiple commits but should still remain traceable to one numbered task.
- Conventional categories such as `feat`, `fix`, or `docs` may still be used in PR descriptions or changelogs, but the commit subject itself should stay task-numbered.

### 1.5 Verification Commands

Run these throughout delivery:

```powershell
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
.\backend\mvnw.cmd spring-boot:run
git status --short --branch
```

When Docker is available:

```powershell
docker compose up -d
.\backend\mvnw.cmd test
```

## 2. Target Repository Shape

By the end of full plan execution, the repository should contain at minimum:

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
- `backend/src/main/java/com/stridehub/refund`
- `backend/src/main/java/com/stridehub/audit`
- `backend/src/main/java/com/stridehub/notification`
- `backend/src/main/resources/db/migration`
- `backend/src/test/java/com/stridehub`
- `frontend/src`
- `docs/*`
- `.github/workflows/*`
- `docker-compose.yml`
- `.env.example`

## 3. Global Engineering Rules

### 3.1 Module Rules

Each business module should use:

- `domain`
- `application`
- `infrastructure`
- `web`

No controller should depend directly on entity internals. No module should reach into another module’s persistence layer directly. Cross-module coordination must happen through application services, interfaces, or domain event publication.

### 3.2 Data Rules

- UUID primary keys
- explicit unique constraints for natural keys
- timestamps on all critical stateful tables
- version column or equivalent concurrency mechanism where stock and financial correctness matter
- no silent state mutation without audit where actions are privileged or money-adjacent

### 3.3 Security Rules

- JWT access tokens must be short-lived
- refresh tokens must rotate
- Argon2 for password hashing
- RBAC enforced at service and endpoint levels
- webhook endpoints must verify signatures
- privileged actions require actor and reason capture

### 3.4 Testing Rules

- Every critical state transition must have at least one automated test
- Concurrency-sensitive stock logic must be tested beyond happy path
- Payment duplicate callback behavior must be proven with tests
- Role boundary tests are mandatory for all admin and seller endpoints

## 4. Dependency Graph

### Program Dependencies

- Program 0 must complete before any business module work
- Program 1 depends on Program 0
- Program 2 depends on Identity, Catalog, and Audit from Programs 0 and 1
- Program 3 depends on Payment, Order, and Audit
- Program 4 depends on all earlier programs

### High-Risk Areas

- inventory reservation
- payment webhook idempotency
- order confirmation
- refund reconciliation
- seller approval and suspension side effects

## 5. Program 0: Engineering Foundation

Program 0 is already complete on the current `main` branch. In practice, the baseline was delivered in six task-numbered commits that grouped related foundation work more coarsely than the original draft breakdown. The sequence below is the canonical history that the repository now follows.

### Task 0.1: Establish Backend Foundation Baseline

**Objective:** Bootstrap a runnable backend foundation that already includes the minimum engineering platform needed to start business module work.

**Files:**

- Modify: `backend/pom.xml`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/application-local.yml`
- Create: `backend/src/main/resources/application-test.yml`
- Create: `backend/src/main/resources/db/migration/V1__identity_catalog_core.sql`
- Create: `backend/src/main/resources/db/migration/V2__commerce_flow.sql`
- Create: `backend/src/main/resources/db/migration/V3__seller_admin_audit.sql`
- Create: `backend/src/main/java/com/stridehub/common/...`
- Create: `backend/src/main/java/com/stridehub/config/...`
- Create: `backend/src/test/java/com/stridehub/config/SecurityConfigTest.java`
- Create: `backend/src/test/java/com/stridehub/integration/FlywayMigrationTest.java`
- Create: `.env.example`
- Create: `docker-compose.yml`

**Delivered Scope:**

1. Profile-based configuration and environment baseline.
2. Shared API response, error handling, correlation ID, and time abstractions.
3. Flyway schema foundation with seed roles.
4. Stateless security baseline and protected/public route tests.

**Verification:**

- `.\backend\mvnw.cmd test`
- `.\backend\mvnw.cmd -q -DskipTests compile`

**Commit Message:**

`task 0.1: establish backend foundation baseline`

### Task 0.2: Add CI Baseline and Quick-Start Instructions

**Objective:** Make the backend foundation buildable and reviewable in an automated pipeline.

**Files:**

- Create: `.github/workflows/build.yml`
- Modify: `README.md`

**Delivered Scope:**

1. GitHub Actions build workflow for backend compile and test.
2. Root quick-start instructions aligned with the backend baseline.

**Verification:**

- Review workflow syntax
- Run local `.\backend\mvnw.cmd test`

**Commit Message:**

`task 0.2: add ci baseline and quick-start instructions`

### Task 0.3: Align Foundation Docs with Implementation Baseline

**Objective:** Clarify the difference between implemented baseline and planned target state before feature work expands.

**Files:**

- Modify: `docs/README.md`
- Create: `docs/audits/2026-04-02-baseline-consistency-audit.md`
- Modify: baseline planning and architecture documents as needed

**Delivered Scope:**

1. Baseline consistency audit.
2. Explicit separation between implemented foundation and future-state plans.
3. Foundation documentation aligned with actual Program 0 code.

**Verification:**

- Manual review of docs against the implemented backend baseline

**Commit Message:**

`task 0.3: align foundation docs with implementation baseline`

### Task 0.4: Move Spring Boot Application into Backend Workspace

**Objective:** Restructure the repository into a cleaner monorepo shape without breaking the backend.

**Files:**

- Move: `.mvn` to `backend/.mvn`
- Move: `mvnw` to `backend/mvnw`
- Move: `mvnw.cmd` to `backend/mvnw.cmd`
- Move: `pom.xml` to `backend/pom.xml`
- Move: `src/` to `backend/src/`
- Create: `backend/README.md`

**Delivered Scope:**

1. Dedicated `backend/` workspace for the Spring Boot application.
2. Backend README for workspace-local execution and verification.
3. CORS and config adjustments needed for future buyer web integration.

**Verification:**

- `.\backend\mvnw.cmd test`
- `.\backend\mvnw.cmd -q -DskipTests compile`

**Commit Message:**

`task 0.4: move spring boot application into backend workspace`

### Task 0.5: Add Buyer Web Workspace Foundation

**Objective:** Introduce a separate frontend workspace so the product can evolve as a full platform rather than a backend-only repo.

**Files:**

- Create: `frontend/`
- Create: `package.json`
- Create: `pnpm-workspace.yaml`
- Create: `pnpm-lock.yaml`
- Modify: `.gitignore`

**Delivered Scope:**

1. Vite + React buyer web workspace.
2. Root pnpm workspace configuration.
3. Initial buyer-facing shell aligned with the product direction.

**Verification:**

- `pnpm --dir frontend lint`
- `pnpm --dir frontend build`

**Commit Message:**

`task 0.5: add buyer web workspace foundation`

### Task 0.6: Align Monorepo Docs and CI Workflow

**Objective:** Bring root documentation, plans, and CI configuration into line with the final `backend/` + `frontend/` monorepo layout.

**Files:**

- Modify: `.github/workflows/build.yml`
- Modify: `README.md`
- Modify: `docs/README.md`
- Modify: `docs/audits/2026-04-02-baseline-consistency-audit.md`
- Modify: `docs/plans/2026-04-02-stridehub-full-production-execution-plan.md`
- Modify: `docs/plans/2026-04-02-stridehub-phase-1-execution-plan.md`

**Delivered Scope:**

1. Monorepo-aware CI workflow for backend and frontend.
2. Root documentation aligned with workspace split.
3. Plan files updated to point future implementation to `Task 1.1`.

**Verification:**

- `git status --short --branch`
- `.\backend\mvnw.cmd test`
- `pnpm --dir frontend lint`
- `pnpm --dir frontend build`

**Commit Message:**

`task 0.6: align monorepo docs and ci workflow`

## 6. Program 1: Commerce Core

### Task 1.1: Implement Identity Domain

**Objective:** Deliver authentication and base account model.

**Files:**

- Create: `backend/src/main/java/com/stridehub/identity/domain/...`
- Create: `backend/src/main/java/com/stridehub/identity/application/...`
- Create: `backend/src/main/java/com/stridehub/identity/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/identity/web/AuthController.java`
- Create: `backend/src/main/java/com/stridehub/identity/web/MeController.java`
- Create: `backend/src/test/java/com/stridehub/identity/...`

**Capabilities:**

- register
- login
- refresh token rotation
- get current profile
- address creation placeholder

**Steps:**

1. Model `User`, `Role`, `RefreshToken`, and `Address`.
2. Seed `BUYER`, `SELLER`, and `ADMIN`.
3. Implement register flow with hashed password.
4. Implement login issuing access and refresh tokens.
5. Implement refresh flow that rotates token pairs.
6. Add `/me` endpoint.

**Verification:**

- Integration tests for register, login, and refresh
- Role assignment tests

**Frontend Companion Scope:**

- add buyer auth entry points for register and login
- wire frontend token lifecycle and current-profile bootstrap
- add signed-in vs signed-out header state
- introduce route guard or protected-layout placeholder for account-aware pages

**Commit Message:**

`task 1.1: implement identity and token lifecycle`

### Task 1.2: Implement Catalog Domain

**Objective:** Build product discovery for active catalog items.

**Files:**

- Create: `backend/src/main/java/com/stridehub/catalog/domain/...`
- Create: `backend/src/main/java/com/stridehub/catalog/application/...`
- Create: `backend/src/main/java/com/stridehub/catalog/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/catalog/web/CatalogController.java`
- Create: `backend/src/test/java/com/stridehub/catalog/...`

**Capabilities:**

- category list
- brand list
- product listing
- product detail by slug
- variant listing in detail
- product status enforcement

**Steps:**

1. Model `Category`, `Brand`, `Product`, `ProductVariant`, and `ProductImage`.
2. Add active-only listing query path.
3. Add filters by category, brand, size, and color.
4. Ensure DTOs expose variant availability summary, not persistence internals.
5. Add sample data or test fixtures.

**Verification:**

- Endpoint tests for listing and detail
- Tests that non-active products are hidden

**Frontend Companion Scope:**

- build buyer home sections from category, brand, and featured product APIs
- build product listing page and product detail page using live catalog responses
- implement filter UI for category, brand, size, and color where the backend already supports it

**Commit Message:**

`task 1.2: add catalog browsing and product detail endpoints`

### Task 1.3: Implement Inventory Domain

**Objective:** Add inventory truth and reservation logic at variant level.

**Files:**

- Create: `backend/src/main/java/com/stridehub/inventory/domain/...`
- Create: `backend/src/main/java/com/stridehub/inventory/application/...`
- Create: `backend/src/main/java/com/stridehub/inventory/infrastructure/...`
- Create: `backend/src/test/java/com/stridehub/inventory/...`

**Capabilities:**

- stock per variant
- create reservation
- commit reservation
- release reservation
- mark reservation expired

**Steps:**

1. Model `InventoryItem` and `InventoryReservation`.
2. Add optimistic locking or equivalent stock update protection.
3. Implement reservation failure when requested quantity exceeds available.
4. Add reservation TTL field and status.
5. Implement commit and release paths.

**Verification:**

- Unit tests for reservation rules
- Concurrency integration test for low-stock race

**Frontend Companion Scope:**

- reflect inventory state in listing cards and product detail screens
- disable unavailable variants and surface low-stock messaging
- keep backend stock truth authoritative; frontend should render hints only

**Commit Message:**

`task 1.3: implement variant-level inventory reservations`

### Task 1.4: Implement Cart Module

**Objective:** Give authenticated buyers a persistent cart.

**Files:**

- Create: `backend/src/main/java/com/stridehub/cart/domain/...`
- Create: `backend/src/main/java/com/stridehub/cart/application/...`
- Create: `backend/src/main/java/com/stridehub/cart/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/cart/web/CartController.java`
- Create: `backend/src/test/java/com/stridehub/cart/...`

**Capabilities:**

- get cart
- add item
- update quantity
- remove item
- recalculate subtotal

**Steps:**

1. Model `Cart` and `CartItem`.
2. Ensure a buyer has one active cart.
3. Validate variant identity and quantity.
4. Return pricing snapshot for display purposes.

**Verification:**

- Cart CRUD API tests
- Ownership isolation tests

**Frontend Companion Scope:**

- implement buyer cart page or cart drawer
- wire add, update, and remove flows against the cart API
- render backend subtotal and validation feedback directly

**Commit Message:**

`task 1.4: add persistent buyer cart management`

### Task 1.5: Implement Checkout Session

**Objective:** Convert a valid cart into a payment-ready reservation-backed checkout session.

**Files:**

- Create: `backend/src/main/java/com/stridehub/checkout/domain/...`
- Create: `backend/src/main/java/com/stridehub/checkout/application/...`
- Create: `backend/src/main/java/com/stridehub/checkout/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/checkout/web/CheckoutController.java`
- Create: `backend/src/test/java/com/stridehub/checkout/...`

**Capabilities:**

- create checkout session
- revalidate active products and current prices
- reserve stock
- compute expiry

**Steps:**

1. Model `CheckoutSession`.
2. Read current cart and validate all items.
3. Recompute final totals from source data.
4. Reserve inventory through inventory service.
5. Persist session status and reservation expiry.

**Verification:**

- Checkout creation tests
- Out-of-stock and inactive-variant tests

**Frontend Companion Scope:**

- implement checkout screen with summary, address shell, and session creation flow
- show revalidation failures clearly when cart assumptions are stale
- render reservation expiry countdown or timeout warning

**Commit Message:**

`task 1.5: implement checkout sessions with stock reservation`

### Task 1.6: Implement Payment Module

**Objective:** Initiate payment and process authoritative callbacks.

**Files:**

- Create: `backend/src/main/java/com/stridehub/payment/domain/...`
- Create: `backend/src/main/java/com/stridehub/payment/application/...`
- Create: `backend/src/main/java/com/stridehub/payment/infrastructure/provider/PaymentProvider.java`
- Create: `backend/src/main/java/com/stridehub/payment/infrastructure/provider/MockPaymentProvider.java`
- Create: `backend/src/main/java/com/stridehub/payment/web/PaymentWebhookController.java`
- Create: `backend/src/test/java/com/stridehub/payment/...`

**Capabilities:**

- create payment request
- record provider reference
- verify signature
- process duplicate-safe webhook events

**Steps:**

1. Model `Payment` and `PaymentAttempt`.
2. Add provider abstraction with initiation and verification APIs.
3. Add mock provider implementation for local development.
4. Store idempotency identifiers for provider events.
5. Publish order-confirmation trigger only once.

**Verification:**

- Duplicate webhook replay tests
- Invalid signature tests

**Frontend Companion Scope:**

- connect the checkout screen to payment initiation
- implement pending, failed, and success payment states
- prepare redirect or local mock-provider handling for the buyer flow

**Commit Message:**

`task 1.6: implement payment request and webhook confirmation`

### Task 1.7: Implement Order Module

**Objective:** Turn confirmed payment into an immutable order snapshot and committed stock.

**Files:**

- Create: `backend/src/main/java/com/stridehub/order/domain/...`
- Create: `backend/src/main/java/com/stridehub/order/application/...`
- Create: `backend/src/main/java/com/stridehub/order/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/order/web/OrderController.java`
- Create: `backend/src/test/java/com/stridehub/order/...`

**Capabilities:**

- create order from successful payment
- persist order item snapshot
- commit reservations
- expose order detail
- initial cancellation guardrails

**Steps:**

1. Model `Order`, `OrderItem`, and `Shipment` placeholder.
2. Create order only from verified payment success.
3. Commit reservations and persist order atomically where possible.
4. Expose order read API for buyer.
5. Ensure duplicate callback does not create duplicate orders.

**Verification:**

- Payment-success-to-order integration test
- Duplicate-order prevention test

**Frontend Companion Scope:**

- implement order confirmation page
- implement buyer order history and order detail views
- display immutable order snapshot data rather than live catalog data

**Commit Message:**

`task 1.7: confirm paid checkouts into orders`

## 7. Program 2: Marketplace Governance

### Task 2.1: Implement Seller Application Workflow

**Objective:** Allow buyers to become sellers through explicit approval.

**Files:**

- Create: `backend/src/main/java/com/stridehub/seller/domain/...`
- Create: `backend/src/main/java/com/stridehub/seller/application/...`
- Create: `backend/src/main/java/com/stridehub/seller/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/seller/web/SellerApplicationController.java`
- Create: `backend/src/test/java/com/stridehub/seller/...`

**Capabilities:**

- submit application
- view own application state

**Steps:**

1. Model `SellerProfile` and `SellerApplication`.
2. Restrict application submission to authenticated users.
3. Prevent duplicate active applications.
4. Expose seller application endpoint.

**Verification:**

- Duplicate application tests
- Auth boundary tests

**Frontend Companion Scope:**

- add seller application submission screen in the buyer account area
- add seller application status display for pending, approved, and rejected cases

**Commit Message:**

`task 2.1: add seller application workflow`

### Task 2.2: Implement Admin Seller Decision Flow

**Objective:** Let admins approve, reject, and suspend sellers safely.

**Files:**

- Create: `backend/src/main/java/com/stridehub/admin/web/AdminSellerController.java`
- Create: `backend/src/main/java/com/stridehub/admin/application/...`
- Create: `backend/src/test/java/com/stridehub/admin/...`

**Capabilities:**

- approve seller
- reject seller
- suspend seller
- restore seller

**Steps:**

1. Enforce admin-only access.
2. Require decision reason.
3. Update seller state and roles.
4. Emit audit and outbox records.

**Verification:**

- Admin-only endpoint tests
- State transition tests

**Frontend Companion Scope:**

- buyer-facing frontend needs only status reflection after admin decisions
- internal admin UI can stay deferred unless the project later adds a separate admin workspace

**Commit Message:**

`task 2.2: implement seller approval and suspension controls`

### Task 2.3: Implement Seller Product Management

**Objective:** Allow approved sellers to manage catalog entries.

**Files:**

- Create: `backend/src/main/java/com/stridehub/seller/web/SellerProductController.java`
- Modify: catalog module application services
- Create: `backend/src/test/java/com/stridehub/seller/product/...`

**Capabilities:**

- create product draft
- create variants
- update product and images
- submit product for review

**Steps:**

1. Restrict product ownership to seller profile.
2. Ensure only approved sellers can create products.
3. Add product workflow transitions: `DRAFT` and `PENDING_REVIEW`.
4. Add seller-owned list endpoint.

**Verification:**

- Tests that unapproved seller cannot publish
- Ownership isolation tests

**Frontend Companion Scope:**

- no buyer-facing page is required immediately
- reserve seller portal work for a future internal frontend slice after buyer commerce flow is stable

**Commit Message:**

`task 2.3: add seller product draft and review submission flows`

### Task 2.4: Implement Admin Product Moderation

**Objective:** Control what becomes public in the catalog.

**Files:**

- Create: `backend/src/main/java/com/stridehub/admin/web/AdminProductController.java`
- Create: `backend/src/test/java/com/stridehub/admin/product/...`

**Capabilities:**

- list products pending review
- approve product
- reject product
- archive active product

**Steps:**

1. Expose moderation queue endpoint.
2. Require reason for rejection and archive.
3. Ensure only approved products become visible in public catalog.
4. Write audit records.

**Verification:**

- Tests for moderation visibility behavior

**Frontend Companion Scope:**

- buyer storefront should react automatically because catalog visibility is backend-driven
- no separate buyer UI task is needed beyond removing any stale mocked product data

**Commit Message:**

`task 2.4: implement product moderation workflow`

### Task 2.5: Implement Seller Inventory Management

**Objective:** Allow sellers to adjust their own stock safely.

**Files:**

- Create: `backend/src/main/java/com/stridehub/seller/web/SellerInventoryController.java`
- Create: `backend/src/test/java/com/stridehub/seller/inventory/...`

**Capabilities:**

- view own SKU inventory
- increase or decrease stock
- disallow negative stock

**Steps:**

1. Limit access to seller-owned variants.
2. Add stock adjustment service with audit.
3. Protect against updates to archived or rejected products where appropriate.

**Verification:**

- Stock adjustment tests
- Ownership and status tests

**Frontend Companion Scope:**

- no buyer UI change is required beyond inventory-aware availability updates already covered earlier
- seller-facing UI remains optional until a dedicated internal frontend track is started

**Commit Message:**

`task 2.5: add seller inventory management endpoints`

## 8. Program 3: Financial and Operational Integrity

### Task 3.1: Implement Audit Logging Foundation

**Objective:** Capture privileged and finance-sensitive system actions.

**Files:**

- Create: `backend/src/main/java/com/stridehub/audit/domain/...`
- Create: `backend/src/main/java/com/stridehub/audit/application/...`
- Create: `backend/src/main/java/com/stridehub/audit/infrastructure/...`
- Create: `backend/src/test/java/com/stridehub/audit/...`

**Capabilities:**

- persist actor, action, target, reason, and correlation ID
- query baseline support for operations

**Steps:**

1. Persist audit rows for seller approval, product moderation, payment webhook processing, order confirmation, and refund handling.
2. Provide query helpers for future admin search.

**Verification:**

- Integration tests proving audit row creation

**Commit Message:**

`task 3.1: add audit trail for privileged and financial actions`

### Task 3.2: Implement Outbox Pattern

**Objective:** Guarantee reliable publication of asynchronous side effects.

**Files:**

- Create: `backend/src/main/java/com/stridehub/common/events/...`
- Create: `backend/src/test/java/com/stridehub/events/...`

**Capabilities:**

- outbox event persistence
- event status tracking
- publisher placeholder or job

**Steps:**

1. Persist outbox records in the same transaction as business changes.
2. Add event types for `order confirmed`, `payment confirmed`, `seller approved`, and `product approved`.
3. Add publisher stub or polling worker skeleton.

**Verification:**

- Tests for outbox row creation on order confirmation

**Commit Message:**

`task 3.2: persist cross-module events through outbox`

### Task 3.3: Implement Refund Domain

**Objective:** Add controlled refund initiation and state management.

**Files:**

- Create: `backend/src/main/java/com/stridehub/refund/domain/...`
- Create: `backend/src/main/java/com/stridehub/refund/application/...`
- Create: `backend/src/main/java/com/stridehub/refund/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/refund/web/RefundController.java`
- Create: `backend/src/test/java/com/stridehub/refund/...`

**Capabilities:**

- request refund
- create provider refund request
- track full and partial refund status

**Steps:**

1. Model `Refund`.
2. Restrict refund eligibility to valid order and payment states.
3. Add admin or support-driven refund initiation path.
4. Update order and payment states accordingly.

**Verification:**

- Refund eligibility tests
- Partial vs full refund tests

**Commit Message:**

`task 3.3: implement refund request and state handling`

### Task 3.4: Implement Reconciliation Hooks

**Objective:** Provide recovery paths when local and provider states diverge.

**Files:**

- Create: `backend/src/main/java/com/stridehub/payment/application/ReconciliationService.java`
- Create: `backend/src/main/java/com/stridehub/payment/application/ReconciliationJob.java`
- Create: `backend/src/test/java/com/stridehub/payment/reconciliation/...`

**Capabilities:**

- list suspect payments
- compare provider and local references
- flag mismatches

**Steps:**

1. Add payment reconciliation status or mismatch result model.
2. Add scheduled job skeleton for nightly or manual reconciliation.
3. Persist mismatch findings for operations review.

**Verification:**

- Reconciliation mismatch tests

**Commit Message:**

`task 3.4: add payment reconciliation hooks and mismatch detection`

### Task 3.5: Implement Support and Operations Search Endpoints

**Objective:** Let operations investigate finance and order incidents without database access.

**Files:**

- Create: `backend/src/main/java/com/stridehub/admin/web/AdminAuditController.java`
- Create: `backend/src/main/java/com/stridehub/admin/web/AdminOrderOpsController.java`
- Create: `backend/src/test/java/com/stridehub/admin/ops/...`

**Capabilities:**

- search audit logs by correlation ID
- inspect payment, order, and refund state bundle
- perform controlled order cancel override where policy allows

**Steps:**

1. Build read models for operational inspection.
2. Add strict admin-only access.
3. Require reason capture for any override action.

**Verification:**

- Admin ops endpoint tests

**Commit Message:**

`task 3.5: add support operations inspection and override tools`

## 9. Program 4: Scale and Production Readiness

### Task 4.1: Add Notification Abstractions

**Objective:** Introduce side-effect boundaries for email and operational events.

**Files:**

- Create: `backend/src/main/java/com/stridehub/notification/...`
- Create: `backend/src/test/java/com/stridehub/notification/...`

**Capabilities:**

- order confirmation email trigger
- seller approval notification trigger
- failure-tolerant integration boundary

**Steps:**

1. Add notification service interface.
2. Publish notification requests from outbox consumer or orchestrator.
3. Keep failures non-blocking for core transaction outcomes.

**Verification:**

- Tests that notification failure does not fail order confirmation

**Commit Message:**

`task 4.1: add notification integration boundary`

### Task 4.2: Add Metrics and Observability Baseline

**Objective:** Expose the health and behavior signals expected of a production service.

**Files:**

- Modify: configuration files
- Create: `backend/src/main/java/com/stridehub/config/ObservabilityConfig.java`
- Create: `backend/src/test/java/com/stridehub/observability/...`

**Required Metrics:**

- checkout success and failure count
- payment webhook success and failure count
- reservation expiry count
- order confirmation latency
- admin override count

**Steps:**

1. Expose management endpoints intentionally.
2. Add counters and timers around high-value workflows.
3. Ensure correlation ID is included in logs.
4. Leave tracing hooks ready for OpenTelemetry.

**Verification:**

- Metrics exposure smoke test

**Commit Message:**

`task 4.2: add metrics and observability baseline`

### Task 4.3: Add Rate Limiting and Abuse Controls

**Objective:** Protect the highest-risk public endpoints.

**Files:**

- Create: `backend/src/main/java/com/stridehub/security/ratelimit/...`
- Create: `backend/src/test/java/com/stridehub/security/ratelimit/...`

**Endpoints to Protect:**

- login
- register
- checkout initiation
- payment webhook endpoint

**Steps:**

1. Add rate-limiting abstraction.
2. Use Redis-backed strategy when available.
3. Add fallback or disabled profile behavior for local development.

**Verification:**

- Rate-limit behavior tests

**Commit Message:**

`task 4.3: add rate limiting for critical endpoints`

### Task 4.4: Add Data Seeding and Demo Fixtures

**Objective:** Make the project easy to review and demo.

**Files:**

- Create: `backend/src/main/java/com/stridehub/config/SeedDataConfig.java`
- Create: `backend/src/main/resources/db/migration/V4__seed_reference_data.sql`

**Seed Scope:**

- roles
- categories
- brands
- a few products and variants
- a demo buyer
- a demo approved seller
- a demo admin

**Verification:**

- Start application locally and verify reviewable demo data

**Commit Message:**

`task 4.4: add reference seed data for local review`

### Task 4.5: Add Production-Grade Documentation Alignment

**Objective:** Ensure docs match shipped behavior exactly.

**Files:**

- Modify: `docs/api/openapi.yaml`
- Modify: `docs/runbooks/*`
- Modify: `docs/test-strategy.md`
- Modify: `docs/release-checklist.md`
- Modify: `README.md`

**Steps:**

1. Align OpenAPI with implemented DTOs and routes.
2. Update runbooks with actual recovery paths.
3. Update README with setup and demo flow.
4. Expand release checklist to match current deployment shape.

**Verification:**

- Manual doc review against implementation

**Commit Message:**

`task 4.5: align operational and api documentation with implementation`

## 10. End-to-End Scenario Suite

The following scenarios are mandatory before declaring the product delivery-ready for its current stage:

### Scenario A: Buyer Purchase

1. Register a buyer.
2. Log in.
3. View products.
4. Add an in-stock variant to cart.
5. Create checkout session.
6. Create payment intent.
7. Simulate successful provider webhook.
8. Confirm order appears in buyer order read model.
9. Confirm the buyer frontend can traverse the same flow without API mocks for completed milestones.

### Scenario B: Duplicate Webhook Safety

1. Use an existing successful payment event.
2. Replay the same webhook event.
3. Confirm the same order is not recreated.
4. Confirm duplicate processing is recorded safely.

### Scenario C: Low Stock Race

1. Set a variant to quantity 1.
2. Start checkout from two sessions.
3. Ensure only one reservation succeeds or only one order confirms.

### Scenario D: Seller Approval and Product Visibility

1. Submit seller application.
2. Approve seller as admin.
3. Create product draft as seller.
4. Approve product as admin.
5. Confirm product becomes visible in public catalog.

### Scenario E: Refund Handling

1. Confirm a paid order exists.
2. Initiate refund.
3. Confirm payment and order states update.
4. Confirm audit entries exist.

## 11. Definition of Done by Program

### Program 0 Done

- app boots with profile-based config
- migrations run cleanly
- security baseline in place
- CI baseline exists

### Program 1 Done

- buyer can authenticate, browse, cart, checkout, pay, and see a confirmed order
- duplicate payment callback does not duplicate order confirmation
- stock reservation path exists and is tested
- buyer frontend is integrated through the completed Program 1 API milestones

### Program 2 Done

- seller onboarding and product moderation work end-to-end
- catalog only exposes approved products

### Program 3 Done

- refunds and audit trails exist
- reconciliation mismatch detection exists
- support inspection endpoints exist

### Program 4 Done

- metrics exist
- critical endpoints are rate-limited
- docs and operational playbooks reflect reality

## 12. Final Verification Gate

Before calling the product build-ready, run:

```powershell
git status --short --branch
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
.\backend\mvnw.cmd spring-boot:run
```

Then verify manually:

- auth endpoints behave correctly
- catalog listing works
- checkout flow works
- payment callback path is idempotent
- order details are readable
- seller approval path works
- audit data exists
- metrics endpoints are accessible as intended
- buyer frontend can execute the completed commerce flow against real backend APIs

## 13. Recommended Commit Sequence

1. `task 0.1: establish backend foundation baseline`
2. `task 0.2: add ci baseline and quick-start instructions`
3. `task 0.3: align foundation docs with implementation baseline`
4. `task 0.4: move spring boot application into backend workspace`
5. `task 0.5: add buyer web workspace foundation`
6. `task 0.6: align monorepo docs and ci workflow`
7. `task 1.1: implement identity and token lifecycle`
8. `task 1.2: add catalog browsing and product detail endpoints`
9. `task 1.3: implement variant-level inventory reservations`
10. `task 1.4: add persistent buyer cart management`
11. `task 1.5: implement checkout sessions with stock reservation`
12. `task 1.6: implement payment request and webhook confirmation`
13. `task 1.7: confirm paid checkouts into orders`
14. `task 2.1: add seller application workflow`
15. `task 2.2: implement seller approval and suspension controls`
16. `task 2.3: add seller product draft and review submission flows`
17. `task 2.4: implement product moderation workflow`
18. `task 2.5: add seller inventory management endpoints`
19. `task 3.1: add audit trail for privileged and financial actions`
20. `task 3.2: persist cross-module events through outbox`
21. `task 3.3: implement refund request and state handling`
22. `task 3.4: add payment reconciliation hooks and mismatch detection`
23. `task 3.5: add support operations inspection and override tools`
24. `task 4.1: add notification integration boundary`
25. `task 4.2: add metrics and observability baseline`
26. `task 4.3: add rate limiting for critical endpoints`
27. `task 4.4: add reference seed data for local review`
28. `task 4.5: align operational and api documentation with implementation`

## 14. What Is Intentionally Deferred Beyond This Plan

The following remain outside this full production plan and should only be added through a new decision cycle:

- recommendation engine
- seller payout orchestration
- advanced fraud scoring
- dedicated search service extraction
- warehouse routing engine
- international pricing and tax complexity


