# ADR-003: Reserve Inventory Before Payment Confirmation

## Status
Accepted

## Context

If the system accepts payment before reserving inventory, it risks charging buyers for unavailable stock. This is especially risky in variant-heavy commerce where multiple buyers may compete for the same size or color combination.

## Decision

The system will reserve inventory before final payment confirmation and will commit or release reservations based on payment outcome.

## Consequences

### Positive

- reduces oversell risk
- makes checkout behavior explicit and testable
- provides a clean operational model for expiry and recovery

### Negative

- requires reservation TTL and cleanup jobs
- introduces concurrency complexity in inventory handling

## Follow-up

- implement optimistic locking on hot inventory rows
- document expiration and release policies
