# ADR-004: Use the Outbox Pattern for Cross-Module Domain Events

## Status
Accepted

## Context

The platform needs to trigger asynchronous side effects such as notifications, audit projections, and downstream integrations. Publishing directly to external systems inside a business transaction risks message loss or inconsistent side effects.

## Decision

The system will persist domain events in an outbox within the same transaction as the business state change, then publish them asynchronously.

## Consequences

### Positive

- improves delivery reliability for asynchronous side effects
- supports cleaner separation between core transactions and integrations
- simplifies future service extraction

### Negative

- requires publisher workers or scheduled polling
- adds extra operational components to monitor

## Follow-up

- define outbox retention and retry rules
- expose failed publication metrics
