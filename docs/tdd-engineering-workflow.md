# StrideHub TDD Engineering Workflow

## 1. Goal

StrideHub should use TDD as an engineering workflow, not as a slogan.

The practical objective is:

- define behavior before implementation
- prove the new test fails for the expected reason
- implement only enough code to pass
- keep the evidence visible in the pull request and in CI

## 2. What TDD Means in This Repository

The repository follows the same core rule as the superpower TDD skill:

> no production code without a failing test first

For StrideHub, that means:

1. identify the smallest behavior change for the current task
2. write or update the automated test first
3. run the narrowest command that proves the test fails for the right reason
4. implement the minimum code to make that test pass
5. rerun the targeted command
6. rerun the broader repository verification commands
7. only then commit, request review, and open or update the pull request

## 3. What GitHub Can and Cannot Enforce

### GitHub Can Enforce

- the pull request includes TDD evidence
- local verification evidence is written into the PR
- backend and frontend automated verification rerun in CI
- merge is blocked when required checks fail

### GitHub Cannot Truly Enforce

- whether the test was genuinely written before the code
- whether the author watched the test fail before implementation
- whether the green implementation was truly minimal

Because of that limitation, StrideHub uses a layered process:

- engineer discipline locally
- pull-request governance for written evidence
- CI rerun for independent verification
- release workflow only from a green baseline

## 4. The Delivery Loop

### Step 1: Choose the Smallest Behavior

Work one task or one subtask at a time, for example:

- `task 2.1` seller lifecycle approval
- `task 3.3` refund eligibility rejection
- `task 4.3` rate limiting for login

Do not implement multiple unrelated behaviors in one loop.

### Step 2: RED

Write the failing automated test first.

Examples:

- backend unit test for a policy object
- backend integration test for an endpoint
- frontend component or route test for a new buyer interaction

Run the narrowest proof command first.

Repository examples:

```powershell
.\backend\mvnw.cmd -Dtest=IdentityControllerIntegrationTest test
.\backend\mvnw.cmd -Dtest=RefundServiceTest test
pnpm --dir frontend test --run --reporter=basic src/pages/account/OrderDetailPage.test.tsx
```

A valid RED state means:

- the test fails
- the failure is because the behavior is missing or wrong
- the failure is not caused by a typo, bad fixture, or broken test setup

### Step 3: GREEN

Implement the minimum code required to pass the test.

Do not mix in cleanup, renaming, or future-proofing during GREEN.

Rerun the same narrow command until it passes.

### Step 4: REFACTOR

Once the narrow test is green:

- remove duplication
- improve naming
- extract helpers
- keep behavior unchanged

Then rerun the narrow test again.

### Step 5: BROADER VERIFICATION

Before a commit or completion claim, rerun the broader commands relevant to the change.

The default repository verification baseline is:

```powershell
.\backend\mvnw.cmd verify
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```

For narrower backend-only or frontend-only work, the task owner can run the smaller relevant set locally first, but the PR should still state exactly what was run.

## 5. Pull Request Evidence Standard

Every pull request must document:

- the exact task reference
- whether TDD applied
- the RED command
- the RED failure summary
- the GREEN verification command
- the broader local verification commands

This is enforced through:

- `.github/pull_request_template.md`
- `.github/workflows/pull-request-governance.yml`

If TDD is marked not applicable, the PR must explain why.

Valid examples:

- docs-only changes
- GitHub workflow-only changes
- non-behavioral repository cleanup

Invalid examples:

- "too simple"
- "already manually tested"
- "I added tests after"

## 6. Repository Workflow Layers

### Local Engineer Workflow

This is where TDD actually happens.

1. create or update test
2. run RED command
3. implement minimal change
4. rerun GREEN command
5. refactor
6. run broader verification

### Pull Request Governance

This is where the repository checks that the engineer wrote down evidence.

The governance workflow verifies:

- required PR sections exist
- task reference is present
- TDD applicability is explicitly chosen
- RED/GREEN evidence is documented when TDD is required
- verification, docs, and risk sections are filled

### CI

CI independently reruns:

- backend `mvn verify`
- frontend `lint`
- frontend `test:coverage`
- frontend `build`

CI proves the branch is green.
It does not prove the author followed TDD honestly.

### Release

The release workflow is a separate pipeline.

It reruns packaging-oriented verification, builds release artifacts, and optionally publishes a GitHub Release for version tags.

That separation matters:

- `CI` answers: is this change safe to merge?
- `Release` answers: is this revision packaged and published correctly?

## 7. Branch Protection Recommendation

For `main`, require at least these checks:

- `Pull Request Governance`
- `Backend Verification`
- `Frontend Verification`
- `Quality Gate`
- `Dependency Review`

This gives the repository:

- process evidence
- automated verification
- dependency hygiene

## 8. Repository-Specific Commit Flow

TDD does not require every RED step to be a separate pushed commit.
However, the final commit history should still remain traceable to the execution plans.

Recommended pattern:

- local RED work happens before the first pushed commit
- pushed commits remain task-numbered and meaningful
- PR body carries the TDD evidence that CI cannot infer from git history

Examples:

- `task 2.1: implement seller onboarding lifecycle`
- `task 2.1.1: add seller approval policy tests`
- `task 2.1.2: wire seller lifecycle api and state transitions`

## 9. Review Gate

Before merge:

- request code review
- fix important findings
- rerun verification
- ensure the PR body still reflects the final implementation

TDD without review is still incomplete for production work.
Review catches design and integration issues that a single engineer can miss.

## 10. Practical Rule for StrideHub

Use this mental model:

- `TDD` defines how you build the change
- `PR governance` proves you documented that process
- `CI` proves the branch is green
- `Release workflow` packages and publishes from a green branch or tag

All four layers are required for a production-style workflow.
