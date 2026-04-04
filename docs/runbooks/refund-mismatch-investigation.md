# Runbook: Refund Mismatch Investigation

> Phase note: this runbook is primarily a Program 3 reference. Refund workflows are not part
> of the implemented Phase 1 baseline yet.

## Symptoms

- local refund shows completed but provider dashboard disagrees
- provider refund succeeded but order state is unchanged
- partial refund totals do not match expected order item amounts

## Investigation Checklist

1. Identify the order, payment, and refund records involved.
2. Compare local refund amount against provider refund amount.
3. Verify whether the refund was partial or full.
4. Inspect provider reference, webhook events, and audit trail.

## Common Causes

- missing or delayed refund webhook
- incorrect mapping between refund and payment attempt
- repeated refund initiation without proper idempotency
- order state transition failure after provider success

## Recovery Guidance

- prefer reconciling from provider truth back into local state
- if provider success is confirmed and local update failed, replay the local refund completion path
- if local refund exists but provider has no matching transaction, halt further retries until the idempotency context is understood

## Follow-up

- capture the discrepancy in incident notes
- add a reconciliation rule if this class of mismatch is not already covered
