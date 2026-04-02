# StrideHub Release Checklist

## 1. Scope Confirmation

- release scope is documented
- linked issue or milestone is defined
- impacted modules are known
- rollback expectations are understood

## 2. Pre-Merge Engineering Checks

- code review completed
- tests added or updated
- migrations reviewed
- API contract changes documented
- ADR added if architecture changed materially

## 3. Pre-Release Validation

- all required tests pass
- application starts cleanly in staging
- migrations succeed in a staging-like environment
- payment sandbox flow has been verified
- webhook replay safety has been validated
- operational dashboards show expected signals

## 4. Deployment Readiness

- secrets are configured
- environment variables are documented
- feature flags are set if applicable
- backup and restore posture is confirmed
- on-call or owner is identified for release monitoring

## 5. Post-Deployment Validation

- health endpoints are green
- critical logs show no unexpected errors
- buyer catalog reads work
- cart and checkout entry points respond correctly
- payment webhook endpoint is reachable
- first synthetic order flow succeeds

## 6. Release Completion

- release notes published
- issue tracker updated
- docs updated to match shipped behavior
- follow-up items captured
