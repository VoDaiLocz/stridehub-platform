# StrideHub Master Task Checklist

This file is the single-table execution dashboard for the whole repository. Use it to see every phase, every task, current status, dependency order, and expected deliverable at a glance.

For implementation detail, use these source documents:

- [Full Production Execution Plan](./docs/plans/2026-04-02-stridehub-full-production-execution-plan.md)
- [Detailed Phase 1 Execution Plan](./docs/plans/2026-04-02-stridehub-phase-1-execution-plan.md)
- [Execution Checklist and Progress Tracker](./PROGRESS.md)

## Status Legend

- `[x]` completed
- `[-]` in progress
- `[ ]` not started
- `[!]` blocked

## Current Execution Position

- Current repository baseline: `Program 0 complete`
- Current recommended next task: `Task 1.1`
- Current execution mode: `follow numbered tasks in order`

## Phase Summary

| Program | Phase Name | Scope | Status | Next Gate |
| --- | --- | --- | --- | --- |
| 0 | Engineering Foundation | monorepo, backend baseline, frontend shell, CI, docs alignment | `[x]` | Start `Task 1.1` |
| 1 | Commerce Core | identity, catalog, inventory, cart, checkout, payment, order, seller application, audit/outbox baseline | `[ ]` | Finish Phase 1 entry gate and complete `1.1` through `1.12` |
| 2 | Marketplace Governance | seller lifecycle, admin seller decisions, seller products, moderation, seller inventory | `[ ]` | Start only after Phase 1 exit criteria passes |
| 3 | Financial and Operational Integrity | audit hardening, outbox dispatch, refunds, reconciliation, support tooling | `[ ]` | Start only after payment and order flows are stable |
| 4 | Scale and Production Readiness | notifications, observability, abuse controls, demo fixtures, production docs alignment | `[ ]` | Start only after Programs 1 to 3 are complete |

## Master Checklist Table

| Program | Task | Title | Workspace | Status | Depends On | Main Deliverable | Verification | Commit Target |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0 | 0.1 | Establish Backend Foundation Baseline | `backend/` + root infra | `[x]` | none | Spring Boot baseline, config profiles, Flyway schema, security baseline, shared error conventions, compose env | backend test + compile | `task 0.1` |
| 0 | 0.2 | Add CI Baseline and Quick-Start Instructions | root + `.github/` | `[x]` | `0.1` | GitHub Actions workflow and root quick-start docs | workflow review + backend test | `task 0.2` |
| 0 | 0.3 | Align Foundation Docs with Implementation Baseline | `docs/` | `[x]` | `0.1`, `0.2` | baseline audit and foundation doc alignment | manual docs audit | `task 0.3` |
| 0 | 0.4 | Move Spring Boot Application into Backend Workspace | `backend/` | `[x]` | `0.1` to `0.3` | monorepo split with dedicated backend workspace | backend test + compile | `task 0.4` |
| 0 | 0.5 | Add Buyer Web Workspace Foundation | `frontend/` + root workspace | `[x]` | `0.4` | React/Vite buyer shell and pnpm workspace | frontend lint + build | `task 0.5` |
| 0 | 0.6 | Align Monorepo Docs and CI Workflow | root + `docs/` + `.github/` | `[x]` | `0.4`, `0.5` | docs and CI aligned with `backend/` + `frontend/` monorepo | backend test + frontend lint/build + status clean | `task 0.6` |
| 1 | 1.1 | Implement Identity Domain and Security Model | `backend/identity`, `backend/config` | `[ ]` | `0.6` | register, login, refresh rotation, `/me`, RBAC, Argon2, JWT | auth integration tests + protected endpoint tests | `task 1.1` |
| 1 | 1.2 | Implement Catalog Module | `backend/catalog` | `[ ]` | `1.1` | categories, brands, product listing, product detail, variant detail exposure | catalog API tests | `task 1.2` |
| 1 | 1.3 | Implement Inventory Module | `backend/inventory` | `[ ]` | `1.2` | inventory truth per SKU, reservation create/commit/release, expiry placeholder | inventory rule tests + low-stock concurrency test | `task 1.3` |
| 1 | 1.4 | Implement Cart Module | `backend/cart` | `[ ]` | `1.1`, `1.2` | buyer cart CRUD and cart validation baseline | cart API tests | `task 1.4` |
| 1 | 1.5 | Implement Checkout Session Module | `backend/checkout` | `[ ]` | `1.3`, `1.4` | checkout snapshot, revalidation, reservation orchestration | checkout validation tests | `task 1.5` |
| 1 | 1.6 | Implement Payment Module with Provider Abstraction | `backend/payment` | `[ ]` | `1.5` | payment initiation, provider abstraction, signed webhook handling, idempotency | duplicate webhook tests + signature validation tests | `task 1.6` |
| 1 | 1.7 | Implement Order Module | `backend/order` | `[ ]` | `1.5`, `1.6` | order creation from payment-confirmed checkout, state machine, buyer order access | order lifecycle tests + duplicate confirm tests | `task 1.7` |
| 1 | 1.8 | Implement Seller Application Workflow | `backend/seller` | `[ ]` | `1.1` | seller application submission baseline | seller application tests | `task 1.8` |
| 1 | 1.9 | Implement Audit and Outbox Foundation | `backend/audit` + shared persistence | `[ ]` | `1.6`, `1.7`, `1.8` | initial audit log and outbox persistence for critical transitions | audit/outbox persistence tests | `task 1.9` |
| 1 | 1.10 | Tighten API Documentation and Error Contract | `docs/` + backend web layer | `[ ]` | `1.1` to `1.9` | OpenAPI, path/versioning, error-envelope alignment | docs review against implementation | `task 1.10` |
| 1 | 1.11 | Add Operational Baseline | root + `backend/` + `frontend/` + docs | `[ ]` | `1.1` to `1.10` | local run hardening, fixtures, health visibility, startup guidance | local run-through + docs checks | `task 1.11` |
| 1 | 1.12 | Final Phase 1 Verification and Cleanup | monorepo-wide | `[ ]` | `1.1` to `1.11` | clean, stable Phase 1 baseline | backend test + compile + frontend lint/build + docs review | `task 1.12` |
| 2 | 2.1 | Implement Seller Onboarding Lifecycle | `backend/seller` | `[ ]` | `1.8`, `1.9` | seller profile creation and lifecycle states after review | seller lifecycle tests | `task 2.1` |
| 2 | 2.2 | Implement Admin Seller Decision Flow | `backend/admin`, `backend/seller` | `[ ]` | `2.1` | admin approve, reject, suspend, reactivate seller flow | admin RBAC tests + status transition tests | `task 2.2` |
| 2 | 2.3 | Implement Seller Product Management | `backend/seller`, `backend/catalog` | `[ ]` | `2.1`, `2.2` | seller-owned product CRUD and draft/review lifecycle | ownership tests + seller product API tests | `task 2.3` |
| 2 | 2.4 | Implement Admin Product Moderation | `backend/admin`, `backend/catalog` | `[ ]` | `2.3` | moderation decisions controlling public visibility | moderation tests + public visibility tests | `task 2.4` |
| 2 | 2.5 | Implement Seller Inventory Management | `backend/seller`, `backend/inventory` | `[ ]` | `2.3`, `2.4` | seller-scoped stock management without breaking reservations | seller inventory tests + ownership boundary tests | `task 2.5` |
| 3 | 3.1 | Implement Audit Logging Foundation | `backend/audit` | `[ ]` | `1.9`, `2.2`, `2.4` | richer privileged-action audit model with actor, reason, correlation | audit searchability and persistence tests | `task 3.1` |
| 3 | 3.2 | Implement Outbox Pattern | `backend/audit` or shared infra | `[ ]` | `1.9`, `3.1` | reliable outbox dispatch and retry-safe publication | outbox enqueue/dispatch tests | `task 3.2` |
| 3 | 3.3 | Implement Refund Domain | `backend/refund`, `backend/payment`, `backend/order` | `[ ]` | `1.6`, `1.7`, `3.1` | partial/full refund lifecycle and policy checks | refund tests + payment/order transition tests | `task 3.3` |
| 3 | 3.4 | Implement Reconciliation Hooks | `backend/payment`, `backend/refund` | `[ ]` | `3.3` | mismatch tracking and reconciliation-ready provider references | reconciliation mismatch tests | `task 3.4` |
| 3 | 3.5 | Implement Support and Operations Search Endpoints | `backend/admin`, `backend/audit`, support-facing web APIs | `[ ]` | `3.1` to `3.4` | search for orders, payments, refunds, audits by ops keys | admin authorization tests + search filter tests | `task 3.5` |
| 4 | 4.1 | Add Notification Abstractions | `backend/notification` | `[ ]` | `1.7`, `2.2`, `3.3` | notification service abstraction for order and governance events | notification trigger tests | `task 4.1` |
| 4 | 4.2 | Add Metrics and Observability Baseline | `backend/` + docs | `[ ]` | `1.6`, `1.7`, `3.3` | metrics, structured logs, tracing-ready wiring | local observability checks + docs review | `task 4.2` |
| 4 | 4.3 | Add Rate Limiting and Abuse Controls | `backend/` + Redis-backed protection as needed | `[ ]` | `1.1`, `1.6`, `4.2` | rate limiting for auth, checkout, and webhooks | rate-limit tests | `task 4.3` |
| 4 | 4.4 | Add Data Seeding and Demo Fixtures | root + `backend/` + `frontend/` | `[ ]` | `1.2`, `2.3`, `4.2` | coherent demo data and recruiter-ready seed flow | manual demo verification + startup docs | `task 4.4` |
| 4 | 4.5 | Add Production-Grade Documentation Alignment | `docs/` + root docs | `[ ]` | `1.12`, `2.5`, `3.5`, `4.1` to `4.4` | final docs aligned to shipped state, not just plan | docs audit | `task 4.5` |

## Phase Entry and Exit Gates

| Phase | Entry Gate | Exit Gate |
| --- | --- | --- |
| Program 0 | empty repo or rebuild scenario | backend baseline, frontend shell, CI, and docs aligned |
| Program 1 | Program 0 complete | identity, catalog, inventory, cart, checkout, payment, order, seller application, audit/outbox baseline complete |
| Program 2 | Program 1 complete | seller lifecycle, moderation, seller product and inventory governance complete |
| Program 3 | Program 2 complete and payment/order flow stable | refunds, reconciliation hooks, ops search, stronger audit/outbox complete |
| Program 4 | Programs 1 to 3 complete | observability, abuse controls, notifications, demo fixtures, and final docs alignment complete |

## Immediate Next Steps

| Priority | Action | Why |
| --- | --- | --- |
| 1 | Finalize API prefix and versioning decision before `1.1` | current docs and baseline endpoint style still need to be fully consistent |
| 2 | Update OpenAPI so identity paths include `/auth/register`, `/auth/login`, `/auth/refresh`, and `/me` consistently | prevents Task 1.1 from drifting away from contract |
| 3 | Start `Task 1.1` implementation | this is the first unfinished business task in the plan |
