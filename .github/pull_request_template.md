## Summary

- Describe the product or engineering change.
- Link the task number or execution-plan item.

## Verification

- [ ] `.\backend\mvnw.cmd verify`
- [ ] `pnpm --dir frontend lint`
- [ ] `pnpm --dir frontend test:coverage`
- [ ] `pnpm --dir frontend build`

## Contract and Docs

- [ ] API contract updated if backend behavior changed
- [ ] Docs and runbooks updated if operational behavior changed

## Release Risk

- [ ] Migration impact reviewed
- [ ] Auth/payment/inventory risks reviewed
- [ ] Rollback path understood
