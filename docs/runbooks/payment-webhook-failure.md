# Runbook: Payment Webhook Failure

> Phase note: this runbook is partially applicable in the current Phase 1 backend because
> signed webhook processing already exists, but later-phase tooling such as richer dashboards
> and replay controls may still be missing.

## Symptoms

- orders remain in `PENDING_PAYMENT`
- payment provider dashboard shows success but local order is unconfirmed
- webhook error rate spikes

## Likely Causes

- invalid webhook signature
- provider retries due to timeout
- database write failure during processing
- idempotency record not persisted

## Immediate Actions

1. Check recent application logs filtered by webhook correlation ID or provider reference.
2. Verify the provider event exists and was signed correctly.
3. Confirm whether the event was already processed and only the acknowledgment failed.
4. Replay the webhook only if idempotency storage is healthy.

## Recovery Rules

- never create an order manually without checking payment state first
- if the event is valid and unprocessed, replay through the supported operational path
- if the event was already processed, do not force a duplicate confirmation

## Post-Incident

- record the event ID, provider reference, and timeline
- identify whether the root cause was network, signature, storage, or logic
- add or extend alerting if the signal was weak
