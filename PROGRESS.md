# StrideHub Execution Checklist

This file is the day-to-day execution checklist for the repository. It is meant to be used as the operational companion to the detailed planning documents, not as a replacement for them.

## Purpose

- Track what is already complete in the codebase.
- Make the next actionable task obvious.
- Provide a phase-by-phase checklist that can be followed from the current monorepo baseline through production-readiness.
- Keep implementation, verification, documentation updates, and commit history aligned.

## Canonical Source Documents

- [Full Production Execution Plan](./docs/plans/2026-04-02-stridehub-full-production-execution-plan.md)
- [Detailed Phase 1 Execution Plan](./docs/plans/2026-04-02-stridehub-phase-1-execution-plan.md)
- [Architecture](./docs/architecture.md)
- [Domain Model](./docs/domain-model.md)
- [OpenAPI Specification](./docs/api/openapi.yaml)
- [Documentation Index](./docs/README.md)

## Status Legend

- `[x]` completed
- `[-]` in progress
- `[ ]` not started
- `[!]` blocked or requires design decision before proceeding

## Current Repository Status

- [x] Monorepo structure exists with `backend/` and `frontend/`.
- [x] Program 0 foundation is complete.
- [x] Backend compile and test baseline already runs.
- [x] Frontend buyer companion baseline exists for auth, catalog, cart, checkout, and buyer order pages.
- [x] Program 1 commerce core is implemented through Task `1.12`.
- [x] Current recommended next task: `Task 2.1`.

## Task Execution Workflow

Use this mini-checklist for every numbered task before moving to the next one.

- [ ] Read the matching section in the full production plan.
- [ ] If the task belongs to Phase 1, also read the matching section in the phase-1 execution plan.
- [ ] Confirm dependency tasks are already completed in this file.
- [ ] Confirm current API contract and naming still match the implementation plan.
- [ ] Identify all files that will be created or modified before coding.
- [ ] Implement code and automated tests together.
- [ ] Run verification commands relevant to the task.
- [ ] Update docs or OpenAPI if the implementation changes behavior or contracts.
- [ ] Commit with task-numbered history such as `task 1.2: ...` or `task 1.2.1: ...`.
- [ ] Mark the task as completed in this file only after verification passes.

## Standard Verification Commands

```powershell
.\backend\mvnw.cmd test
.\backend\mvnw.cmd -q -DskipTests compile
pnpm --dir frontend lint
pnpm --dir frontend test --run
pnpm --dir frontend build
git status --short --branch
```

If local infrastructure is needed:

```powershell
docker compose up -d
```

## Program 0 / Phase 0: Engineering Foundation

These tasks are already complete in the current repository history.

### Task 0.1: Establish Backend Foundation Baseline

- [x] Spring Boot backend workspace exists.
- [x] Base dependencies for web, security, validation, JPA, Redis, Flyway, and OpenAPI exist.
- [x] `application.yml`, `application-local.yml`, and `application-test.yml` exist.
- [x] Flyway baseline migrations exist.
- [x] Shared API error and correlation foundation exists.
- [x] Stateless security baseline exists.
- [x] Root `.env.example` and `docker-compose.yml` exist.
- [x] Verification completed.
- [x] Commit recorded as `task 0.1: establish backend foundation baseline`.

### Task 0.2: Add CI Baseline and Quick-Start Instructions

- [x] GitHub Actions workflow exists.
- [x] Root quick-start documentation exists.
- [x] Local verification path is documented.
- [x] Verification completed.
- [x] Commit recorded as `task 0.2: add ci baseline and quick-start instructions`.

### Task 0.3: Align Foundation Docs with Implementation Baseline

- [x] Baseline audit document exists.
- [x] Docs distinguish implemented baseline from target-state planning.
- [x] Foundation docs are aligned with Program 0.
- [x] Verification completed.
- [x] Commit recorded as `task 0.3: align foundation docs with implementation baseline`.

### Task 0.4: Move Spring Boot Application into Backend Workspace

- [x] Maven wrapper moved under `backend/`.
- [x] `src/` moved under `backend/`.
- [x] Backend workspace README exists.
- [x] Backend still compiles and tests after workspace move.
- [x] Commit recorded as `task 0.4: move spring boot application into backend workspace`.

### Task 0.5: Add Buyer Web Workspace Foundation

- [x] `frontend/` workspace exists.
- [x] Root `package.json` and `pnpm-workspace.yaml` exist.
- [x] Buyer web shell exists.
- [x] Frontend lint and build baseline runs.
- [x] Commit recorded as `task 0.5: add buyer web workspace foundation`.

### Task 0.6: Align Monorepo Docs and CI Workflow

- [x] Docs now describe the monorepo shape correctly.
- [x] CI understands backend and frontend workspaces.
- [x] Plans point implementation to the first unfinished task instead of redoing foundation work.
- [x] Commit recorded as `task 0.6: align monorepo docs and ci workflow`.
- [x] Follow-up numbering alignment exists as `task 0.6.1: align plan numbering with repository history`.

### Program 0 Exit Checklist

- [x] Backend foundation is stable enough for business module work.
- [x] Frontend workspace exists for later buyer UI development.
- [x] CI, docs, and monorepo layout are aligned.
- [x] The next task was clearly defined as `Task 1.1`.

## Program 1 / Phase 1: Commerce Core

This phase is complete in the current repository history. Keep it as the completion ledger for the shipped Phase 1 baseline.

### Phase 1 Entry Gate

- [x] Program 0 is complete.
- [x] API versioning and route prefix are finalized consistently before exposing new business endpoints.
- [x] OpenAPI identity contract includes `/auth/register`, `/auth/login`, `/auth/refresh`, and `/me` in the same style used by implementation.

### Task 1.1: Implement Identity Domain and Security Model

**Goal:** deliver authentication, refresh-token rotation, RBAC, and current-profile access.

- [x] Create `identity/domain`, `identity/application`, `identity/infrastructure`, and `identity/web`.
- [x] Implement `User`, `Role`, `RefreshToken`, and `Address`.
- [x] Ensure role seeding strategy remains consistent with Flyway baseline.
- [x] Implement Argon2 password hashing.
- [x] Implement JWT issue and validation flow.
- [x] Implement register endpoint.
- [x] Implement login endpoint.
- [x] Implement refresh-token rotation endpoint.
- [x] Implement current user endpoint.
- [x] Add integration tests for register, login, refresh, and protected `/me`.
- [x] Update OpenAPI and auth docs to match the implemented endpoint shape.
- [x] Commit as `task 1.1: implement authentication and rbac foundation`.

### Task 1.2: Implement Catalog Module

**Goal:** expose buyer-facing product discovery for active products and variants.

- [x] Create catalog package structure.
- [x] Implement `Category`, `Brand`, `Product`, `ProductVariant`, and `ProductImage`.
- [x] Add repository queries for listing and detail by slug.
- [x] Support category, brand, size, and color filters.
- [x] Return DTOs instead of exposing entities directly.
- [x] Ensure only `ACTIVE` products are exposed publicly.
- [x] Add test fixtures or seed strategy for catalog scenarios.
- [x] Add API tests for product listing and detail.
- [x] Update OpenAPI catalog paths and examples.
- [x] Commit as `task 1.2: add catalog browsing and product detail endpoints`.

### Task 1.3: Implement Inventory Module

**Goal:** introduce stock truth and reservation lifecycle per variant SKU.

- [x] Create inventory package structure.
- [x] Implement `InventoryItem`.
- [x] Implement `InventoryReservation`.
- [x] Add optimistic locking or equivalent stock concurrency control.
- [x] Implement reservation create logic with insufficient-stock failure path.
- [x] Implement reservation commit logic.
- [x] Implement reservation release logic.
- [x] Add expiry timestamps and release hook placeholder.
- [x] Add low-stock concurrency integration tests.
- [x] Commit as `task 1.3: implement sku inventory and reservation lifecycle`.

### Task 1.4: Implement Cart Module

**Goal:** allow buyers to build carts without turning cart state into the pricing source of truth.

- [x] Create cart package structure.
- [x] Implement `Cart` and `CartItem`.
- [x] Support add item, update quantity, remove item, clear cart, and get current cart.
- [x] Validate variant existence and basic availability before accepting line items.
- [x] Keep stored cart pricing revalidatable rather than final.
- [x] Add tests for cart mutation and validation behavior.
- [x] Update OpenAPI cart contract.
- [x] Commit as `task 1.4: add buyer cart management flow`.

### Task 1.5: Implement Checkout Session Module

**Goal:** convert a mutable cart into a validated checkout snapshot and inventory reservation request.

- [x] Create checkout package structure.
- [x] Implement `CheckoutSession`.
- [x] Snapshot buyer address, shipping, and pricing inputs needed for checkout.
- [x] Revalidate price and stock at checkout start.
- [x] Create inventory reservations for selected items.
- [x] Support cancel or timeout path that releases reservations.
- [x] Add tests for stale cart data and reservation failure.
- [x] Update OpenAPI checkout contract.
- [x] Commit as `task 1.5: add checkout session and validation flow`.

### Task 1.6: Implement Payment Module with Provider Abstraction

**Goal:** support payment initiation and webhook-driven payment confirmation safely.

- [x] Create payment package structure.
- [x] Implement `Payment` and `PaymentAttempt`.
- [x] Introduce provider abstraction interface for external gateway integration.
- [x] Implement payment intent or equivalent initiation API.
- [x] Implement signed webhook callback handling.
- [x] Make webhook processing idempotent.
- [x] Ensure webhook does not create duplicate confirmations.
- [x] Add tests for duplicate callback behavior and signature validation.
- [x] Update OpenAPI payment and webhook contract.
- [x] Commit as `task 1.6: implement payment provider abstraction and webhook flow`.

### Task 1.7: Implement Order Module

**Goal:** create and advance orders only from confirmed payment outcomes.

- [x] Create order package structure.
- [x] Implement `Order` and `OrderItem`.
- [x] Build order creation path from checkout and payment records.
- [x] Enforce order state machine for initial `PAID` order confirmation.
- [x] Commit inventory reservations only after payment success.
- [x] Support buyer order detail and order listing access.
- [x] Add tests for payment-confirmed order creation and duplicate webhook safety.
- [x] Update OpenAPI orders contract.
- [x] Commit as `task 1.7: implement order creation and lifecycle state machine`.

### Task 1.8: Implement Seller Application Workflow

**Goal:** add the first seller-facing onboarding path required before marketplace governance.

- [x] Implement `SellerApplication`.
- [x] Expose buyer-to-seller application submission endpoint.
- [x] Expose current seller application status endpoint.
- [x] Validate seller application payload and prevent duplicate active applications.
- [x] Prepare admin review path hooks for later tasks through baseline entities and repositories.
- [x] Add tests for submit and duplicate prevention.
- [x] Update OpenAPI seller application contract.
- [x] Commit as `task 1.8: add seller application submission workflow`.

### Task 1.9: Implement Audit and Outbox Foundation

**Goal:** add the base operational integrity layer needed before later governance and refund flows.

- [x] Implement `AuditLog`.
- [x] Implement `OutboxEvent`.
- [x] Capture privileged actor and reason where applicable.
- [x] Record key domain events for payment, order, and seller-application transitions.
- [x] Add tests that audit and outbox entries are persisted on critical transitions.
- [x] Update architecture and operations docs if event publishing behavior changes.
- [x] Commit as `task 1.9: add audit trail and outbox persistence foundation`.

### Task 1.10: Tighten API Documentation and Error Contract

**Goal:** align runtime behavior, OpenAPI contract, and shared error envelope.

- [x] Review all Phase 1 endpoints for path consistency.
- [x] Ensure API versioning is documented consistently.
- [x] Align OpenAPI schemas with implemented request and response DTOs.
- [x] Align authentication requirements in OpenAPI security sections.
- [x] Ensure error payload examples match actual backend behavior.
- [x] Commit as `task 1.10: align phase 1 api contract and error conventions`.

### Task 1.11: Add Operational Baseline

**Goal:** make the Phase 1 system easier to run, inspect, and debug locally.

- [x] Add any missing local seeds or demo fixtures required for manual verification.
- [x] Improve actuator, health, and local visibility where needed.
- [x] Confirm Docker Compose services are sufficient for local Phase 1 flows.
- [x] Ensure backend and frontend startup instructions remain correct.
- [x] Update relevant docs and runbooks for the implemented baseline.
- [x] Commit as `task 1.11: add phase 1 operational baseline hardening`.

### Task 1.12: Final Phase 1 Verification and Cleanup

**Goal:** close Phase 1 with a stable, reviewable, and documented baseline.

- [x] Run full backend test suite.
- [x] Run backend compile verification.
- [x] Run frontend lint, test, and build verification.
- [x] Remove dead code, unused properties, and stale placeholders that no longer match the baseline.
- [x] Confirm docs do not claim more than what has actually been implemented.
- [x] Confirm task status in this file is accurate.
- [x] Commit recorded as `task 1.12: finalize phase 1 verification and cleanup`.

### Phase 1 Exit Checklist

- [x] Identity, catalog, inventory, cart, checkout, payment, and order flow are implemented.
- [x] Seller application exists.
- [x] Audit and outbox foundation exist.
- [x] OpenAPI and runtime behavior are aligned.
- [x] Phase 1 verification commands pass.
- [x] Phase 1 docs are aligned with actual implementation.

## Program 2 / Phase 2: Marketplace Governance

### Task 2.1: Implement Seller Application Workflow

**Goal:** move seller onboarding from submission-only to governed lifecycle.

- [ ] Expand seller onboarding domain from the Phase 1 baseline.
- [ ] Implement seller profile creation after approval.
- [ ] Define seller status transitions `PENDING`, `APPROVED`, `REJECTED`, `SUSPENDED`.
- [ ] Ensure seller-facing endpoints honor approval state.
- [ ] Add tests for seller status transitions.
- [ ] Commit as `task 2.1: implement seller onboarding lifecycle`.

### Task 2.2: Implement Admin Seller Decision Flow

**Goal:** give admin users controlled approval and suspension powers.

- [ ] Add admin endpoints to approve or reject seller applications.
- [ ] Require actor and reason capture for decisions.
- [ ] Add admin suspension and reactivation flow.
- [ ] Ensure suspended sellers lose access to protected seller capabilities.
- [ ] Add role boundary tests for admin-only actions.
- [ ] Commit as `task 2.2: add admin seller approval and suspension controls`.

### Task 2.3: Implement Seller Product Management

**Goal:** let approved sellers manage their own product catalog inside governance boundaries.

- [ ] Add seller product CRUD endpoints.
- [ ] Support draft and pending-review product states.
- [ ] Add seller-owned product image handling strategy.
- [ ] Prevent sellers from manipulating other sellers' products.
- [ ] Add tests for ownership boundaries and product-state rules.
- [ ] Commit as `task 2.3: add seller product management workflow`.

### Task 2.4: Implement Admin Product Moderation

**Goal:** place public catalog exposure behind admin review control.

- [ ] Add admin product moderation endpoints.
- [ ] Support product status transitions `DRAFT -> PENDING_REVIEW -> ACTIVE | REJECTED | ARCHIVED`.
- [ ] Ensure rejected or archived products are hidden from public catalog APIs.
- [ ] Require actor and reason capture for moderation actions.
- [ ] Add tests for moderation side effects.
- [ ] Commit as `task 2.4: add admin product moderation workflow`.

### Task 2.5: Implement Seller Inventory Management

**Goal:** let sellers maintain stock safely without breaking reservation integrity.

- [ ] Add seller inventory endpoints scoped to owned variants.
- [ ] Support stock increase and decrease workflows.
- [ ] Preserve concurrency safety around reserved stock.
- [ ] Add tests for seller ownership boundaries and stock rules.
- [ ] Commit as `task 2.5: add seller inventory management endpoints`.

### Phase 2 Exit Checklist

- [ ] Seller approval lifecycle is fully operational.
- [ ] Seller dashboard APIs exist for products and inventory.
- [ ] Admin can moderate sellers and products.
- [ ] Ownership and RBAC tests pass.
- [ ] Public catalog exposure respects moderation state.

## Program 3 / Phase 3: Financial and Operational Integrity

### Task 3.1: Implement Audit Logging Foundation

- [ ] Harden audit model for support, admin, and finance-sensitive actions.
- [ ] Ensure important transitions store actor, reason, correlation ID, and timestamps.
- [ ] Add searchability requirements needed by later support workflows.
- [ ] Add tests covering audit persistence for privileged actions.
- [ ] Commit as `task 3.1: expand audit logging for privileged workflows`.

### Task 3.2: Implement Outbox Pattern

- [ ] Expand outbox usage beyond baseline persistence into reliable publication flow.
- [ ] Add publisher or dispatcher mechanism with retry-safe behavior.
- [ ] Ensure payment and order events are publishable without dual-write issues.
- [ ] Add tests for outbox enqueue and dispatch semantics.
- [ ] Commit as `task 3.2: implement reliable outbox event dispatch`.

### Task 3.3: Implement Refund Domain

- [ ] Create refund package structure.
- [ ] Implement `Refund`.
- [ ] Add refund eligibility policy checks.
- [ ] Support full and partial refund flows.
- [ ] Update payment and order states during refund lifecycle.
- [ ] Return stock when policy allows and fulfillment state permits.
- [ ] Add tests for partial and full refund behavior.
- [ ] Commit as `task 3.3: implement refund workflow and policy checks`.

### Task 3.4: Implement Reconciliation Hooks

- [ ] Add reconciliation record or hook model needed for payment consistency checks.
- [ ] Store external provider identifiers required for settlement comparison.
- [ ] Add scheduled or placeholder reconciliation workflow.
- [ ] Surface reconciliation mismatch states without mutating order truth incorrectly.
- [ ] Add tests for mismatch recording behavior.
- [ ] Commit as `task 3.4: add payment reconciliation hooks and mismatch tracking`.

### Task 3.5: Implement Support and Operations Search Endpoints

- [ ] Add support search endpoints for orders, payments, refunds, and audits.
- [ ] Support lookup by order number, payment reference, user, and correlation ID.
- [ ] Add admin-only authorization coverage.
- [ ] Ensure outputs are safe for operational use and do not leak secrets.
- [ ] Add tests for support search authorization and filtering.
- [ ] Commit as `task 3.5: add support and operations search tooling`.

### Phase 3 Exit Checklist

- [ ] Refund flow is implemented and tested.
- [ ] Reconciliation mismatch signals exist.
- [ ] Audit and outbox are operationally useful.
- [ ] Support/admin search tools exist.
- [ ] Finance-sensitive flows are traceable and idempotent.

## Program 4 / Phase 4: Scale and Production Readiness

### Task 4.1: Add Notification Abstractions

- [ ] Create notification package structure.
- [ ] Introduce notification abstraction for email or future async channels.
- [ ] Publish order and seller-governance notifications through defined interfaces.
- [ ] Avoid hard-coding delivery providers into domain logic.
- [ ] Add tests for notification trigger behavior.
- [ ] Commit as `task 4.1: add notification abstraction layer`.

### Task 4.2: Add Metrics and Observability Baseline

- [ ] Add metrics around checkout, payment, refunds, and admin interventions.
- [ ] Ensure logs remain structured and correlation-aware.
- [ ] Prepare tracing hooks or OpenTelemetry-ready wiring.
- [ ] Add docs for how to inspect local operational signals.
- [ ] Commit as `task 4.2: add metrics and observability baseline`.

### Task 4.3: Add Rate Limiting and Abuse Controls

- [ ] Add rate limiting for login, register, checkout, and webhook endpoints.
- [ ] Add abuse control hooks for suspicious repeated operations.
- [ ] Ensure legitimate retries remain possible where idempotency exists.
- [ ] Add tests for rate-limit enforcement.
- [ ] Commit as `task 4.3: add abuse controls and endpoint rate limiting`.

### Task 4.4: Add Data Seeding and Demo Fixtures

- [ ] Add coherent demo seed data for catalog, sellers, and orders.
- [ ] Ensure demo data supports manual QA and recruiter demos.
- [ ] Keep seed strategy safe for non-demo environments.
- [ ] Update quick-start docs for demo usage.
- [ ] Commit as `task 4.4: add demo fixtures and seed data workflows`.

### Task 4.5: Add Production-Grade Documentation Alignment

- [ ] Reconcile docs against the fully implemented baseline.
- [ ] Update architecture, domain, API, runbook, and release docs to remove stale planning assumptions.
- [ ] Ensure docs distinguish clearly between shipped scope and roadmap scope.
- [ ] Confirm this checklist matches real completion status.
- [ ] Commit as `task 4.5: finalize production-ready documentation alignment`.

### Phase 4 Exit Checklist

- [ ] Notifications, observability, abuse controls, and demo data are in place.
- [ ] Production-facing docs are aligned with implementation.
- [ ] The repository is ready for end-to-end demo and portfolio presentation.

## Final Completion Checklist

- [x] All tasks marked complete reflect reality in the codebase.
- [x] OpenAPI matches implemented endpoints.
- [x] Backend tests and compile checks pass.
- [x] Frontend lint, test, and build checks pass.
- [x] Docs do not overstate implementation status.
- [x] Commit history remains traceable to numbered tasks.

## Execution Log

- `2026-04-03 22:55 +07:00` Frontend companion milestones were recorded as `task 1.1.2`, `1.2.2`, `1.3.2`, `1.4.2`, `1.5.2`, and `1.7.2` for auth, catalog shell, inventory-aware product cards, cart UX, checkout screen, and buyer order success UX.
- `2026-04-04 00:54 +07:00` Task `1.5` verified locally with `.\mvnw.cmd test` (`63` tests, `0` failures) and `.\mvnw.cmd -q -DskipTests compile` before commit.
- `2026-04-04 01:05 +07:00` Task `1.5.1` added checkout expiry scheduling and reservation release handling before payment follow-up wiring.
- `2026-04-04 01:13 +07:00` Task `1.6` verified locally with `.\mvnw.cmd test` (`67` tests, `0` failures), `.\mvnw.cmd -q -DskipTests compile`, and payment-focused test coverage before commit.
- `2026-04-04 01:30 +07:00` Task `1.7` verified locally with `.\mvnw.cmd -Dtest=OrderControllerIntegrationTest test`, `.\mvnw.cmd test` (`69` tests, `0` failures), and `.\mvnw.cmd -q -DskipTests compile` before commit.
- `2026-04-04 03:15 +07:00` Task `1.7.3` completed the buyer Phase 1 companion baseline with protected buyer orders, collection routes, product detail, typed API clients, and stronger session/cart contexts.
- `2026-04-04 01:39 +07:00` Task `1.8` verified locally with `.\mvnw.cmd -Dtest=SellerApplicationControllerIntegrationTest test` and `.\mvnw.cmd -q -DskipTests compile` before commit.
- `2026-04-04 01:50 +07:00` Task `1.9` verified locally with `.\mvnw.cmd -Dtest=AuditOutboxIntegrationTest test`, `.\mvnw.cmd test` (`75` tests, `0` failures), and `.\mvnw.cmd -q -DskipTests compile` before commit.
- `2026-04-04 03:28 +07:00` Task `1.10` aligned OpenAPI, runtime route prefixes, and shared error-envelope expectations, including contract coverage for `/v3/api-docs`.
- `2026-04-04 03:31 +07:00` Task `1.11` finalized Phase 1 operational baseline with local seed data, updated startup guidance, and baseline runbook/test-strategy alignment.
- `2026-04-04 03:42 +07:00` Phase 1 final verification passed with backend tests (`78` tests, `0` failures), backend compile, frontend lint, frontend tests (`5` passing), and frontend production build.
