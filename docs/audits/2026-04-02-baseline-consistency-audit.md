# StrideHub Baseline Consistency Audit

## 1. Purpose

This audit reconciles the production-oriented documentation set with the actual
state of the codebase after the Program 0 foundation baseline was implemented.
The goal is to prevent the repository from overstating what already exists and
to make the difference between `implemented baseline` and `target-state plan`
explicit.

## 2. Audit Scope

Reviewed artifacts:

- `pom.xml`
- `src/main/resources/application*.yml`
- `docker-compose.yml`
- `.github/workflows/build.yml`
- `README.md`
- `docs/README.md`
- `docs/architecture.md`
- `docs/test-strategy.md`
- `docs/api/openapi.yaml`
- execution plans under `docs/plans/`

## 3. Summary

Status: `aligned with documented baseline after remediation`

The repository is now consistent at the foundation level, but several
forward-looking documents still describe later business phases. Those documents
are acceptable as long as they are treated as planned work rather than shipped
behavior.

## 4. Resolved Inconsistencies

### 4.1 Starter Scaffold vs Planned Foundation

Previous issue:

- Plans assumed profile-based configuration, Compose infrastructure, migrations,
  security baseline, and CI.
- The repository only contained the default Spring Initializr scaffold.

Resolution:

- Added `application.yml`, `application-local.yml`, `application-test.yml`,
  `docker-compose.yml`, `.env.example`, baseline migrations, security
  configuration, shared API/error conventions, and Program 0 tests.

### 4.2 CI/CD Mentioned in Plans but Missing in Repo

Previous issue:

- The plans required GitHub Actions build verification, but `.github/workflows`
  did not exist.

Resolution:

- Added a GitHub Actions `build.yml` workflow.

### 4.3 Test Strategy Assumed Containerized Integration Too Early

Previous issue:

- The test strategy referenced Testcontainers and PostgreSQL/Redis verification
  without clarifying the current baseline.

Resolution:

- Documented that current CI uses the `test` profile with H2 in PostgreSQL
  compatibility mode.
- Left PostgreSQL/Redis container-backed tests as planned work for later
  database-sensitive modules.

### 4.4 OpenAPI Contract Could Be Misread as Fully Implemented

Previous issue:

- `docs/api/openapi.yaml` models the first business slice, but the runtime
  application only exposes Program 0 foundation endpoints so far.

Resolution:

- Added explicit wording that the committed OpenAPI file is a design-time target
  contract for later slices, while runtime `/v3/api-docs` currently reflects
  only implemented baseline endpoints.

### 4.5 Security Language Was Ahead of Implementation

Previous issue:

- Plans described a JWT-heavy system, which could be misread as full auth being
  already available.

Resolution:

- The codebase now includes stateless security posture and JWT resource-server
  wiring.
- Login, register, refresh rotation, and role lifecycle remain scheduled for
  Program 1 and are not represented as implemented behavior in baseline docs.

### 4.6 Local Infrastructure Description Was Underspecified

Previous issue:

- Architecture docs referred to mock email and object storage in local
  environments, but the repo did not provision them.

Resolution:

- Added Mailpit and MinIO to `docker-compose.yml`.
- Documented that these services are provisioned for local development, but the
  application has not yet integrated them in business modules.

## 5. Remaining Intentional Gaps

These are not contradictions. They are planned capabilities that remain
unimplemented:

- identity flows beyond security baseline
- catalog browsing and product detail endpoints
- cart and checkout orchestration
- payment initiation and webhook handling
- seller onboarding and moderation
- refunds, reconciliation, notifications, outbox consumption, observability, and
  support tooling

## 6. Dependency Notes

- The runtime baseline uses `Spring Boot 4.0.5`.
- Runtime OpenAPI exposure is enabled through `springdoc-openapi` on the Spring
  Boot 4-compatible line.
- Tests intentionally avoid external infrastructure in CI until the persistence
  and concurrency-heavy modules justify containerized verification.

## 7. Risk Notes

Potential future drift points:

- `docs/api/openapi.yaml` may diverge from implemented controllers if feature
  delivery does not update the spec in the same task.
- execution plans may become stale if actual commit sequence materially changes
  and docs are not updated as part of the same work package.
- security documentation must be revisited once token issuance and refresh
  rotation are implemented.

## 8. Required Discipline Going Forward

- Any new implemented endpoint must update both runtime behavior and the
  design-time contract or clearly document why they differ.
- Any new environment dependency must be reflected in `README.md`,
  `.env.example`, and `docker-compose.yml` together.
- Any shift from H2 baseline verification to Testcontainers-backed CI must be
  recorded in `docs/test-strategy.md` and the build workflow.
