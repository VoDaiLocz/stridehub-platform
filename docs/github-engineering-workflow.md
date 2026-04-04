# StrideHub GitHub Engineering Workflow

## 1. Goal

The StrideHub GitHub workflow should look and behave like a production engineering workflow rather than a student repository.

The operating principles are:

- every change should be traceable to a task in the execution plans
- every pull request should carry verification evidence
- `main` should remain releasable
- CI should produce actionable artifacts, not just red or green badges

## 2. Branching Model

### Main Branch

- `main` is the protected integration branch
- direct pushes should be rare and limited to repository maintenance or urgent fixes
- product work should land through pull requests whenever possible

### Feature Branches

Recommended branch naming:

- `task/1.1-auth-foundation`
- `task/1.4-cart-management`
- `task/2.1-seller-approval`
- `fix/ci-test-cleanup`

Each branch should align to one task or one tightly scoped subtask.

## 3. Commit Convention

Commit subjects must remain traceable to the execution plans.

Examples:

- `task 1.1: implement authentication and rbac foundation`
- `task 1.4.2: add buyer cart experience and api integration`
- `task 1.12.1: centralize integration test database cleanup`

Rules:

- one commit should represent one meaningful unit of work
- if multiple commits are needed under one task, add a subtask suffix such as `1.5.1` or `1.5.2`
- do not mix unrelated fixes into one commit

## 4. Pull Request Standard

Each pull request should include:

- a concise summary
- the linked task number
- exact verification commands run locally
- note of any API, migration, or operational impact
- rollback awareness for risky changes

The repository PR template is designed to enforce this baseline.

## 5. Required GitHub Checks

The repository CI baseline should require the following checks before merge:

- `Backend Verification`
- `Frontend Verification`
- `Quality Gate`
- `Dependency Review` on pull requests that modify dependency manifests

Recommended branch protection on `main`:

- require pull request before merge
- require status checks to pass
- dismiss stale approvals on new commits
- require conversation resolution before merge

## 6. CI Workflow Design

### Backend Verification

The backend job is responsible for:

- checking out the repository
- setting up Temurin JDK 21
- running `mvn verify`
- producing JaCoCo coverage reports
- uploading surefire and coverage artifacts

### Frontend Verification

The frontend job is responsible for:

- setting up Node.js 22
- activating `pnpm` through Corepack
- installing dependencies using the workspace lockfile
- running ESLint
- running Vitest coverage
- building the buyer web for production
- uploading coverage artifacts

### Quality Gate

The quality-gate job is the final merge-facing check.

Its role is to:

- depend on backend and frontend verification
- fail if either verification job fails
- provide a single required check for branch protection

## 7. CI Artifacts

Artifacts should be treated as part of the engineering workflow, not as optional extras.

Current artifact expectations:

- backend surefire reports
- backend JaCoCo HTML report
- frontend coverage output

Artifacts are useful for:

- debugging remote-only test failures
- investigating flaky tests
- preserving evidence for review

## 8. Dependency Review

Pull requests should automatically review dependency changes.

This helps surface:

- risky package upgrades
- vulnerable transitive additions
- unnecessary dependency churn

The dependency-review workflow is intentionally lightweight and should run on every PR into `main`.

## 9. When CI Fails

The engineer responsible for the change should:

1. identify the failing job and step
2. retrieve the failing logs or artifacts
3. reproduce the issue locally when possible
4. fix the root cause rather than masking the symptom
5. rerun verification locally before pushing

If the failure is remote-only:

- compare OS/runtime differences
- inspect artifact output
- avoid merging on the assumption that the failure is transient

## 10. Release Readiness Relationship

CI passing is necessary but not sufficient for release.

Before production release, the release checklist still applies:

- scope confirmation
- migration review
- sandbox payment verification
- operational readiness
- post-deployment validation

CI proves engineering correctness.
Release readiness proves operational safety.
