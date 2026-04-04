# Runbook: Stuck Order Recovery

> Phase note: treat this as a target-state guide. Phase 1 already has order confirmation,
> reservation commit, and audit/outbox baseline behavior, but not the full operational tooling
> assumed by every step below.

## Symptoms

- order remains in an intermediate state longer than expected
- inventory reservations remain active beyond TTL
- payment is captured but fulfillment has not started

## Investigation Checklist

1. Inspect the order state, payment state, and reservation state together.
2. Check outbox publication status for the order confirmation event.
3. Confirm whether any background job or worker failed.
4. Review audit logs for admin or support interventions.

## Safe Recovery Paths

- if payment succeeded and reservation is still active, prefer controlled order confirmation recovery
- if payment failed or expired, release reservations before retrying checkout
- if order was already confirmed but downstream side effects failed, replay side effects instead of mutating the order blindly

## Unsafe Actions

- do not decrement stock manually without checking reservation history
- do not mark an order as paid based only on buyer UI evidence
- do not replay payment confirmation without idempotency context

## Escalation

Escalate to engineering review when:

- payment state and order state disagree
- reservation records are missing
- duplicate provider events exist without clear resolution
