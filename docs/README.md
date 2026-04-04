# StrideHub Documentation

StrideHub is a production-style `multi-vendor footwear marketplace` delivered as a monorepo with separate backend and frontend workspaces. This documentation set is written to resemble a real engineering documentation package used for product delivery, architecture review, implementation planning, and operations handoff.

## Documentation Map

- [Master Task Checklist](../TASK-CHECKLIST.md)
- [Execution Checklist and Progress Tracker](../PROGRESS.md)
- [Product Requirements Document](./prd.md)
- [Enterprise Delivery Plan](./plans/2026-04-02-stridehub-enterprise-plan.md)
- [Detailed Phase 1 Execution Plan](./plans/2026-04-02-stridehub-phase-1-execution-plan.md)
- [Full Production Execution Plan](./plans/2026-04-02-stridehub-full-production-execution-plan.md)
- [Architecture](./architecture.md)
- [Domain Model](./domain-model.md)
- [OpenAPI Specification](./api/openapi.yaml)
- [GitHub Engineering Workflow](./github-engineering-workflow.md)
- [TDD Engineering Workflow](./tdd-engineering-workflow.md)
- [Test Strategy](./test-strategy.md)
- [Release Checklist](./release-checklist.md)
- [Baseline Consistency Audit](./audits/2026-04-02-baseline-consistency-audit.md)
- [Runbook Index](./runbooks/README.md)
- [ADR Index](./adr/README.md)

## Recommended Reading Order

1. `prd.md`
2. `audits/2026-04-02-baseline-consistency-audit.md`
3. `plans/2026-04-02-stridehub-full-production-execution-plan.md`
4. `plans/2026-04-02-stridehub-phase-1-execution-plan.md`
5. `plans/2026-04-02-stridehub-enterprise-plan.md`
6. `architecture.md`
7. `domain-model.md`
8. `api/openapi.yaml`
9. `github-engineering-workflow.md`
10. `tdd-engineering-workflow.md`
11. `test-strategy.md`
12. `runbooks/*`
13. `adr/*`

## Scope of This Documentation Set

This repository currently combines two documentation modes:

- `implemented baseline` documentation for the foundation already present in the codebase
- `target-state planning` documentation for the product roadmap that will be implemented in later phases

### Implemented Baseline

- Program 0 engineering foundation
- Program 1 backend commerce core through seller application and audit/outbox baseline
- Program 1 buyer-facing frontend companion baseline for auth, catalog, checkout shell, and order views
- monorepo repository split with `backend/` and `frontend/`
- runtime configuration and environment setup
- baseline schema migrations
- security and request-correlation foundation
- CI build verification
- PR template and dependency review workflow
- pull-request governance workflow with TDD evidence checks
- dedicated release workflow for packaging and GitHub Release publication

### Target-State Planning

- Product framing and success criteria
- System design and architectural rationale
- Domain model and workflow definitions
- API contract baseline
- Operational guidance for core failure scenarios
- Test and release discipline

## Delivery Scope by Phase

### Phase 1

- Identity and access
- Catalog and product variants
- Inventory and reservation model
- Cart and checkout
- Buyer companion screens for auth, catalog browsing, product detail, checkout shell, and order history
- External payment gateway integration pattern
- Payment webhook processing
- Order confirmation
- Seller application baseline
- Audit and outbox persistence baseline

### Phase 2

- Seller onboarding and approval
- Product moderation workflow
- Seller inventory management
- Seller fulfillment workflow
- Review and rating foundations

### Phase 3

- Refund orchestration
- Payment reconciliation
- Support tooling and operational dashboards
- Audit search and interventions

### Phase 4

- Search service
- Recommendation engine
- Seller payouts
- Fraud and risk scoring
- Multi-warehouse support

## Documentation Standards

- English is used for all engineering-facing documentation.
- Mermaid diagrams are used so architecture and workflows remain version-controlled and diff-friendly.
- ADRs capture decisions that materially affect implementation or operations.
- API documentation follows an OpenAPI-first approach.
- Commit history is expected to follow plan-linked numbering such as `task 0.6: ...` or `task 1.1.2: ...` so implementation remains traceable to execution plans.
- Files under `plans/` describe intended delivery order; they are not claims that every item is already implemented.
- The enterprise plan remains useful for product framing, but the full execution plan, phase checklist, and task dashboard are the canonical sources for current implementation order.
