# StrideHub Test Strategy

## 1. Test Philosophy

StrideHub should be tested as a commerce platform, not just as a REST API. The primary objective is to prove business correctness for money-adjacent and stock-adjacent workflows.

The most critical risks are:

- duplicate payment confirmation
- oversell during concurrent checkout
- broken order state transitions
- authorization leaks between buyer, seller, and admin roles

## 1.1 TDD Delivery Rule

StrideHub should follow a test-first delivery loop for behavior changes:

1. write or update the smallest failing automated test first
2. run the narrowest command that proves the test fails for the expected reason
3. implement the minimum code to pass
4. rerun the targeted command
5. rerun broader repository verification before commit or PR update

The authoritative repository guidance for this workflow lives in [TDD Engineering Workflow](./tdd-engineering-workflow.md).

## 2. Test Pyramid

### Unit Tests

Focus:

- pricing and discount rules
- state machine transitions
- reservation rules
- refund policy validation
- seller lifecycle policy

### Integration Tests

Focus:

- database repositories
- Flyway migrations
- transaction boundaries
- webhook idempotency storage
- inventory concurrency behaviors

### API Tests

Focus:

- authentication flows
- cart and checkout endpoints
- seller and admin authorization
- validation and error envelope consistency

### End-to-End Scenario Tests

Focus:

- full buyer purchase flow
- duplicate payment webhook handling
- stock contention handling
- seller suspension behavior
- refund request lifecycle

## 3. Test Environments

### Local

- fast feedback
- lightweight smoke tests
- mocked or simulated third-party dependencies

### CI

- migration validation
- full API and integration suite
- deterministic seed data

Current baseline:

- The current GitHub Actions workflow verifies backend quality gates through `mvn verify` against the `test` profile.
- The current GitHub Actions workflow also verifies frontend lint, frontend coverage tests, and frontend production build.
- The `test` profile uses H2 in PostgreSQL compatibility mode to keep Program 0 verification deterministic and independent of Docker.
- PostgreSQL and Redis container-backed tests remain planned for later database-sensitive modules.
- The current Phase 1 baseline includes automated coverage for identity, catalog, cart, checkout, payment, order, seller-application, and audit/outbox flows.
- CI uploads backend surefire reports, backend JaCoCo coverage, and frontend coverage artifacts for remote debugging and review.

### Pre-Release / Staging

- sandbox payment provider
- seeded seller, buyer, and catalog data
- operational scenario rehearsals

## 4. Priority Scenario Matrix

### P0 Scenarios

- buyer registers, checks out, pays, and sees an order confirmed
- repeated success webhook does not create duplicate order confirmation
- two buyers compete for a low-stock variant and only one succeeds
- seller application submission is persisted once and cannot be duplicated per user

### P1 Scenarios

- payment timeout releases reservations
- suspended seller products disappear from public catalog
- refund request transitions correctly

### P2 Scenarios

- reporting and projections consume outbox events correctly
- notification delivery failure does not break order confirmation

## 5. Quality Gates

- no merge without automated tests for new critical business rules
- no release without migration verification
- no payment-related change without webhook replay tests
- no authorization change without role coverage tests
- no completion claim without fresh verification evidence

## 6. Recommended Tooling

- JUnit 5
- Spring Boot Test
- Spring Security Test
- Testcontainers for PostgreSQL and Redis where available
- MockMvc or RestAssured for HTTP API coverage
