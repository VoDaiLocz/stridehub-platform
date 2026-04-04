## Task Reference

- Link the task number exactly, for example `task 2.1`.

## Summary

- Describe the product or engineering change.
- Summarize the intended behavior and scope.

## TDD Applicability

- [ ] TDD required for this change
- [ ] TDD not applicable (docs-only, workflow-only, or other non-production-code change)

## TDD Evidence

- RED command: ``
- RED result:
- GREEN command(s): ``
- Refactor notes:
- Reason not applicable:

## Verification

Paste the exact commands and outcomes you ran locally.

```text
.\backend\mvnw.cmd verify
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```

## Contract and Docs

Note OpenAPI, docs, runbooks, or ADR impacts.

## Release Risk

Capture migration, auth, payment, inventory, rollback, or operational risk.
