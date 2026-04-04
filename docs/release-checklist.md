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
- CI required checks are green (`Backend Verification`, `Frontend Verification`, `Quality Gate`)
- dependency review is green for PRs that touch dependency manifests

## 3. Pre-Release Validation

- all required tests pass
- application starts cleanly in staging
- migrations succeed in a staging-like environment
- payment sandbox flow has been verified
- webhook replay safety has been validated
- operational dashboards show expected signals
- release workflow has produced backend and frontend artifacts successfully

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

## 7. Release Workflow Notes

- CI and release are intentionally separate workflows.
- CI is the merge gate for engineering correctness.
- The release workflow packages backend and frontend artifacts and publishes a GitHub Release for version tags.
- Tagged releases should use semantic version tags such as `v1.2.0`.
