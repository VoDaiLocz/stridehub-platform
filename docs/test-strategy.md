# StrideHub Test Strategy

## 1. Test Philosophy

StrideHub should be tested as a commerce platform, not just as a REST API. The primary objective is to prove business correctness for money-adjacent and stock-adjacent workflows.

The most critical risks are:

- duplicate payment confirmation
- oversell during concurrent checkout
- broken order state transitions
- authorization leaks between buyer, seller, and admin roles

## 2. Test Pyramid

### Unit Tests

Focus:

- pricing and discount rules
- state machine transitions
- reservation rules
- refund policy validation
- seller approval policy

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

- The current GitHub Actions workflow verifies compile and test against the `test` profile.
- The `test` profile uses H2 in PostgreSQL compatibility mode to keep Program 0 verification deterministic and independent of Docker.
- PostgreSQL and Redis container-backed tests remain planned for later database-sensitive modules.

### Pre-Release / Staging

- sandbox payment provider
- seeded seller, buyer, and catalog data
- operational scenario rehearsals

## 4. Priority Scenario Matrix

### P0 Scenarios

- buyer registers, checks out, pays, and sees an order confirmed
- repeated success webhook does not create duplicate order confirmation
- two buyers compete for a low-stock variant and only one succeeds
- admin approval changes seller capability

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

## 6. Recommended Tooling

- JUnit 5
- Spring Boot Test
- Spring Security Test
- Testcontainers for PostgreSQL and Redis where available
- MockMvc or RestAssured for HTTP API coverage
