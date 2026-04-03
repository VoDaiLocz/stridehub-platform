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
6. Add `/api/v1/identity/me` endpoint.

**Verification:**

- Integration tests for register, login, and refresh
- Role assignment tests

**Frontend Companion Task 1.1-FE**

**Objective:**

Deliver the buyer-facing authentication shell that consumes the identity APIs and establishes session-aware UI state.

**Files:**

- Create or modify: `frontend/src/features/auth/...`
- Create or modify: `frontend/src/features/session/...`
- Create or modify: `frontend/src/components/layout/...`
- Create or modify: `frontend/src/lib/api/...`
- Modify: `frontend/src/App.tsx`

**Required Features:**

- buyer register screen
- buyer login screen
- current-user bootstrap
- signed-in vs signed-out header state
- protected route or protected layout placeholder

**Implementation Steps:**

1. Add frontend API helpers for `/api/v1/identity/register`, `/api/v1/identity/login`, `/api/v1/identity/refresh`, and `/api/v1/identity/me`.
2. Create auth state and current-user bootstrap behavior.
3. Build register and login screens with backend error rendering.
4. Add signed-in vs signed-out header logic.
5. Add protected-layout or route-guard behavior for account-aware pages.

**Verification:**

- buyer auth screens work against real APIs
- protected pages are gated correctly
- current-user bootstrap restores session state as intended

**Suggested Frontend Commit Message:**

`task 1.1.2: add buyer auth screens and session bootstrap`

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

**Frontend Companion Task 1.2-FE**

**Objective:**

Turn the buyer web shell into a real browsing experience driven by catalog APIs.

**Files:**

- Create or modify: `frontend/src/features/home/...`
- Create or modify: `frontend/src/features/catalog/...`
- Create or modify: `frontend/src/pages/...`
- Create or modify: `frontend/src/lib/api/...`

**Required Features:**

- home sections backed by real data
- product listing page
- product detail page
- filter and sort UI
- loading, empty, and error states

**Implementation Steps:**

1. Add catalog API helpers for categories, brands, listing, and detail.
2. Build home sections from category or featured-product responses.
3. Build listing page using backend filters where available.
4. Build product detail page with gallery, metadata, and variant options.
5. Add loading, empty, and error states across catalog screens.

**Verification:**

- buyer listing and detail pages render real API data
- filter inputs map correctly to backend query behavior
- hidden products do not leak into buyer UI

**Suggested Frontend Commit Message:**

`task 1.2.2: add buyer catalog listing and product detail pages`

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

**Frontend Companion Task 1.3-FE**

**Objective:**

Expose inventory-aware storefront behavior without moving stock logic into the frontend.

**Files:**

- Modify: `frontend/src/features/catalog/...`
- Modify: `frontend/src/features/home/...`
- Modify: `frontend/src/components/...`

**Required Features:**

- stock badges
- low-stock messaging
- unavailable variant disabling
- inventory-aware CTA states

**Implementation Steps:**

1. Map backend availability data into storefront view models.
2. Add stock badges to listing cards and featured sections.
3. Disable unavailable variants in product detail.
4. Surface inventory-aware CTA states such as low-stock or out-of-stock.

**Verification:**

- storefront availability matches backend data
- impossible variant options cannot be selected
- add-to-cart entry points respect stock state

**Suggested Frontend Commit Message:**

`task 1.3.2: add inventory-aware storefront states`

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

**Frontend Companion Task 1.4-FE**

**Objective:**

Add a persistent buyer cart experience wired to authenticated cart APIs.

**Files:**

- Create or modify: `frontend/src/features/cart/...`
- Modify: `frontend/src/features/catalog/...`
- Modify: `frontend/src/components/layout/...`
- Modify: `frontend/src/lib/api/...`

**Required Features:**

- cart page or cart drawer
- add-to-cart flow
- quantity update
- remove item
- subtotal rendering
- cart validation feedback

**Implementation Steps:**

1. Add cart API helpers for read, add, update, and remove.
2. Build cart page or cart drawer.
3. Wire add-to-cart from product detail into cart state.
4. Add quantity update and remove-item flows.
5. Render backend subtotal and validation feedback.

**Verification:**

- buyer can manage cart through the UI
- cart state remains synchronized with backend responses
- unauthorized cart access is blocked correctly

**Suggested Frontend Commit Message:**

`task 1.4.2: add buyer cart experience and api integration`

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

**Frontend Companion Task 1.5-FE**

**Objective:**

Create the buyer checkout screen and connect it to checkout-session orchestration.

**Files:**

- Create or modify: `frontend/src/features/checkout/...`
- Modify: `frontend/src/features/cart/...`
- Modify: `frontend/src/lib/api/...`

**Required Features:**

- checkout page
- checkout summary
- session creation trigger
- backend validation error rendering
- reservation expiry UX

**Implementation Steps:**

1. Add checkout-session API helpers.
2. Build checkout page with summary and address shell.
3. Trigger checkout-session creation from the UI.
4. Render revalidation failures clearly when cart assumptions are stale.
5. Show reservation expiry or timeout state if supplied.

**Verification:**

- checkout page creates valid sessions
- stale cart conditions show readable errors
- timeout information renders correctly where available

**Suggested Frontend Commit Message:**

`task 1.5.2: add buyer checkout screen and validation flow`

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

**Frontend Companion Task 1.6-FE**

**Objective:**

Integrate payment initiation and buyer payment states without hard-coding provider internals into the frontend.

**Files:**

- Modify: `frontend/src/features/checkout/...`
- Create or modify: `frontend/src/features/payment/...`
- Modify: `frontend/src/lib/api/...`

**Required Features:**

- payment initiation
- pending state
- failed state
- success handoff state
- local mock-provider compatibility

**Implementation Steps:**

1. Add payment-initiation API integration.
2. Show pending state while payment is being created or processed.
3. Show recoverable failure state when payment initiation fails.
4. Handle redirect, callback, or mock-provider flow as required by backend implementation.
5. Transition cleanly into order-confirmation handling on success.

**Verification:**

- payment initiation is triggered correctly
- pending and failure states behave correctly
- successful payment path proceeds to order confirmation

**Suggested Frontend Commit Message:**

`task 1.6.2: integrate checkout payment states in buyer flow`

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

**Frontend Companion Task 1.7-FE**

**Objective:**

Expose stable post-purchase screens so the buyer can review successful orders independently of mutable catalog state.

**Files:**

- Create or modify: `frontend/src/features/orders/...`
- Modify: `frontend/src/features/payment/...`
- Modify: `frontend/src/pages/...`
- Modify: `frontend/src/lib/api/...`

**Required Features:**

- order confirmation page
- order history page
- order detail page
- immutable order snapshot rendering

**Implementation Steps:**

1. Add order API helpers for order history and detail.
2. Build order confirmation screen after successful payment.
3. Build order history and order detail screens in the buyer account area.
4. Render stable order snapshot data instead of live catalog data.

**Verification:**

- buyer can see confirmed orders after payment success
- order history and detail load from backend APIs
- order screens remain correct even if catalog data changes later

**Suggested Frontend Commit Message:**

`task 1.7.2: add buyer order confirmation and history pages`

**Commit Message:**

`task 1.7: confirm paid checkouts into orders`

### Program 1 Continuation Tasks

Program 1 continues beyond `Task 1.7`. The detailed execution definitions for the remaining
Phase 1 tasks live in the dedicated Phase 1 plan and the task dashboard, but the canonical
task sequence is:

- `Task 1.8`: seller-application submission baseline
- `Task 1.9`: audit and outbox persistence baseline
- `Task 1.10`: API contract and error-envelope alignment
- `Task 1.11`: operational baseline hardening and docs alignment
- `Task 1.12`: final Phase 1 verification and cleanup

Program 2 starts only after those Phase 1 tasks are complete.

## 7. Program 2: Marketplace Governance

### Task 2.1: Implement Seller Onboarding Lifecycle

**Objective:** Expand the Phase 1 seller-application baseline into a governed seller lifecycle.

**Files:**

- Create: `backend/src/main/java/com/stridehub/seller/domain/...`
- Create: `backend/src/main/java/com/stridehub/seller/application/...`
- Create: `backend/src/main/java/com/stridehub/seller/infrastructure/...`
- Create: `backend/src/main/java/com/stridehub/seller/web/SellerApplicationController.java`
- Create: `backend/src/test/java/com/stridehub/seller/...`

**Capabilities:**

- create governed seller profile after approval
- manage seller status transitions
- expose approval-driven seller capability boundaries

**Steps:**

1. Build on the existing `SellerApplication` baseline from Program 1.
2. Create seller profile records after approval or equivalent onboarding transition.
3. Define seller status transitions such as `PENDING`, `APPROVED`, `REJECTED`, and `SUSPENDED`.
4. Ensure seller-facing capabilities are blocked until approval is complete.

**Verification:**

- seller lifecycle transition tests
- seller capability boundary tests

**Frontend Companion Task 2.1-FE**

**Objective:**

Extend the buyer account experience from seller-application tracking into seller-onboarding state visibility.

**Files:**

- Create or modify: `frontend/src/features/seller-application/...`
- Modify: `frontend/src/features/account/...`
- Modify: `frontend/src/lib/api/...`

**Required Features:**

- seller onboarding state screen
- approved, rejected, and suspended states
- continuity from the existing Phase 1 application flow

**Implementation Steps:**

1. Reuse the Phase 1 seller-application screens as the starting point.
2. Add state handling for approved, rejected, and suspended outcomes.
3. Ensure buyer account screens reflect the governed seller lifecycle correctly.
4. Keep admin decision UI out of buyer-facing scope.

**Verification:**

- buyer-facing account UI reflects governed seller status correctly
- no admin-only controls appear in the buyer web

**Suggested Frontend Commit Message:**

`task 2.1.2: extend buyer account seller onboarding states`

**Commit Message:**

`task 2.1: implement seller onboarding lifecycle`

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

**Frontend Companion Task 2.2-FE**

**Objective:**

Reflect seller decision outcomes in buyer-facing account UI while keeping internal admin tooling out of scope.

**Files:**

- Modify: `frontend/src/features/seller-application/...`
- Modify: `frontend/src/features/account/...`

**Required Features:**

- approved, rejected, and suspended status rendering
- no public admin controls

**Implementation Steps:**

1. Update seller-application status rendering to cover admin decision outcomes.
2. Ensure buyer-facing UI exposes status only, not privileged decision controls.
3. Reserve dedicated admin UI for a future internal workspace if needed.

**Verification:**

- buyer can see seller decision outcomes correctly
- no admin-only actions appear in buyer-facing screens

**Suggested Frontend Commit Message:**

`task 2.2.2: reflect seller decision status in account ui`

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

**Frontend Companion Task 2.3-FE**

**Objective:**

Document and preserve the boundary that seller-portal functionality remains deferred while buyer commerce flow stays the priority.

**Files:**

- No mandatory buyer-web file changes
- Optional future internal workspace planning only

**Required Features:**

- none for current buyer web

**Implementation Steps:**

1. Keep seller product tooling out of the buyer-facing app for now.
2. Avoid mixing internal seller workflows into public storefront routes.
3. Revisit only when a dedicated seller-facing UI track begins.

**Verification:**

- buyer storefront remains focused on public commerce features
- no partial seller-portal UI leaks into buyer routes

**Suggested Frontend Commit Message:**

`task 2.3.2: preserve buyer-storefront boundary for seller tooling`

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

**Frontend Companion Task 2.4-FE**

**Objective:**

Ensure the buyer storefront reacts correctly to moderation-controlled catalog visibility without extra manual toggles.

**Files:**

- Modify as needed: `frontend/src/features/catalog/...`
- Modify as needed: `frontend/src/features/home/...`

**Required Features:**

- storefront visibility driven by backend catalog responses
- no stale moderated product mocks

**Implementation Steps:**

1. Remove any stale mocked product data that can override backend visibility.
2. Confirm storefront screens render only what public APIs return.
3. Keep moderation concerns out of buyer-facing controls.

**Verification:**

- moderated products appear or disappear solely based on backend visibility
- buyer UI does not expose moderation internals

**Suggested Frontend Commit Message:**

`task 2.4.2: align storefront visibility with moderated catalog state`

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

**Frontend Companion Task 2.5-FE**

**Objective:**

Preserve the separation between buyer inventory visibility and deferred seller inventory tooling.

**Files:**

- No mandatory buyer-web file changes

**Required Features:**

- none beyond previously delivered inventory-aware buyer UX

**Implementation Steps:**

1. Reuse existing buyer availability rendering.
2. Do not add seller inventory controls into the buyer app.
3. Defer internal seller inventory UI until a dedicated internal frontend exists.

**Verification:**

- buyer inventory UX remains correct
- seller inventory controls are not exposed publicly

**Suggested Frontend Commit Message:**

`task 2.5.2: preserve buyer inventory-only storefront behavior`

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
14. `task 1.8: add seller application submission workflow`
15. `task 1.9: add audit trail and outbox persistence foundation`
16. `task 1.10: align phase 1 api contract and error conventions`
17. `task 1.11: add phase 1 operational baseline hardening`
18. `task 1.12: finalize phase 1 verification and cleanup`
19. `task 2.1: implement seller onboarding lifecycle`
20. `task 2.2: implement seller approval and suspension controls`
21. `task 2.3: add seller product draft and review submission flows`
22. `task 2.4: implement product moderation workflow`
23. `task 2.5: add seller inventory management endpoints`
24. `task 3.1: add audit trail for privileged and financial actions`
25. `task 3.2: persist cross-module events through outbox`
26. `task 3.3: implement refund request and state handling`
27. `task 3.4: add payment reconciliation hooks and mismatch detection`
28. `task 3.5: add support operations inspection and override tools`
29. `task 4.1: add notification integration boundary`
30. `task 4.2: add metrics and observability baseline`
31. `task 4.3: add rate limiting for critical endpoints`
32. `task 4.4: add reference seed data for local review`
33. `task 4.5: align operational and api documentation with implementation`

## 14. What Is Intentionally Deferred Beyond This Plan

The following remain outside this full production plan and should only be added through a new decision cycle:

- recommendation engine
- seller payout orchestration
- advanced fraud scoring
- dedicated search service extraction
- warehouse routing engine
- international pricing and tax complexity


