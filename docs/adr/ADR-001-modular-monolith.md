# ADR-001: Use a Modular Monolith for the Initial Architecture

## Status
Accepted

## Context

StrideHub needs clear domain boundaries, realistic commerce workflows, and operationally credible architecture. At the same time, the initial delivery must remain buildable and maintainable within a single repository and by a small team or single engineer.

## Decision

The system will start as a modular monolith rather than a microservice architecture.

## Consequences

### Positive

- lower operational overhead
- easier local development and debugging
- simpler transactional consistency in early phases
- faster initial delivery

### Negative

- weak internal boundaries could degrade into a tightly coupled monolith
- future extraction into services will require discipline and refactoring

## Follow-up

- enforce package/module boundaries
- capture major future architecture shifts as ADRs
