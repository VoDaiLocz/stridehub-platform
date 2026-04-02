# ADR-002: Treat Payment Webhooks as the Final Source of Truth

## Status
Accepted

## Context

Client-side redirects and browser callbacks are not reliable enough to confirm payments. Users can close the browser, retry requests, or return to the site before the provider has finalized state. Payment gateways, however, provide signed webhooks that represent authoritative settlement outcomes.

## Decision

Signed payment webhooks will be treated as the final source of truth for payment confirmation.

## Consequences

### Positive

- reduces false-positive order confirmation
- supports provider retry semantics safely
- aligns with real payment gateway integration patterns

### Negative

- buyer UX must tolerate temporary processing states
- the system must implement signature verification and idempotency correctly

## Follow-up

- store provider references and webhook event IDs
- ensure webhook handlers are idempotent
- expose operational visibility for delayed callbacks
