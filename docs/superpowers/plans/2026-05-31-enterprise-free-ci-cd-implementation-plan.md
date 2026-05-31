# Enterprise Free-Tier CI/CD Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a professional free-tier CI/CD path for StrideHub using GitHub Actions, Vercel Free for the frontend, and Render Free for the backend.

**Architecture:** Keep CI, preview deployment, and production deployment as separate concerns. GitHub Actions owns deployment orchestration; Vercel and Render dashboard auto-deploys should not bypass quality gates. Backend deploys before frontend production so the React app always points at a checked backend.

**Tech Stack:** GitHub Actions, Vercel CLI, Render deploy hooks, Spring Boot Maven wrapper, pnpm, Vite, React, Markdown runbooks.

---

## File Structure

- Create `vercel.json`: root Vercel monorepo configuration for the Vite frontend.
- Create `render.yaml`: Render blueprint for the Spring Boot backend.
- Create `backend/Dockerfile`: Render Docker build for the backend.
- Create `backend/src/main/resources/application-prod.yml`: production Spring profile sourced from environment variables.
- Create `.github/workflows/vercel-preview.yml`: pull-request preview deployment workflow.
- Create `.github/workflows/deploy-production.yml`: production deployment workflow for Render backend and Vercel frontend.
- Create `docs/vercel-render-cicd.md`: operator runbook for setup, secrets, deployment, verification, and rollback.
- Modify `docs/README.md`: link the new CI/CD runbook.
- Modify `docs/release-checklist.md`: add Vercel/Render production deployment checks.
- Modify `README.md`: link the CI/CD runbook.

## Task 1: Add Vercel Monorepo Config

**Files:**
- Create: `vercel.json`

- [ ] **Step 1: Create Vercel config**

Create `vercel.json` with this content:

```json
{
  "$schema": "https://openapi.vercel.sh/vercel.json",
  "installCommand": "pnpm install --frozen-lockfile",
  "buildCommand": "pnpm --dir frontend build",
  "outputDirectory": "frontend/dist",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

- [ ] **Step 2: Verify JSON syntax**

Run:

```bash
node -e "JSON.parse(require('fs').readFileSync('vercel.json', 'utf8')); console.log('vercel.json ok')"
```

Expected:

```text
vercel.json ok
```

## Task 2: Add Render Backend Blueprint

**Files:**
- Create: `render.yaml`

- [ ] **Step 1: Create Render blueprint**

Create `render.yaml` with this content:

```yaml
services:
  - type: web
    name: stridehub-backend
    runtime: docker
    plan: free
    rootDir: backend
    healthCheckPath: /actuator/health
    dockerfilePath: backend/Dockerfile
    dockerContext: backend
    autoDeployTrigger: off
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: STRIDEHUB_JWT_SECRET
        sync: false
      - key: STRIDEHUB_DB_URL
        sync: false
      - key: STRIDEHUB_DB_USERNAME
        sync: false
      - key: STRIDEHUB_DB_PASSWORD
        sync: false
      - key: STRIDEHUB_WEB_ALLOWED_ORIGINS
        sync: false
      - key: STRIDEHUB_REDIS_HOST
        sync: false
      - key: STRIDEHUB_REDIS_PORT
        sync: false
      - key: STRIDEHUB_REDIS_HEALTH_ENABLED
        value: "false"
```

- [ ] **Step 2: Add backend Dockerfile if absent**

If `backend/Dockerfile` does not exist, create it with this content:

```dockerfile
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline
COPY src src
RUN ./mvnw -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system stridehub && adduser --system --ingroup stridehub stridehub
COPY --from=build /workspace/target/*.jar /app/stridehub.jar
USER stridehub
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/stridehub.jar"]
```

- [ ] **Step 3: Verify Dockerfile path**

Run:

```bash
test -f backend/Dockerfile && test -f render.yaml && echo "render config ok"
```

Expected:

```text
render config ok
```

- [ ] **Step 4: Add production Spring profile**

Create `backend/src/main/resources/application-prod.yml` with environment-backed datasource, Redis, mail, health, and CORS settings so Render does not use local defaults in production.

## Task 3: Add Vercel Preview Workflow

**Files:**
- Create: `.github/workflows/vercel-preview.yml`

- [ ] **Step 1: Create preview workflow**

Create `.github/workflows/vercel-preview.yml` with jobs that:

- run backend Maven verification
- run frontend install, lint, test coverage, and build
- skip deployment for forked pull requests
- deploy Vercel Preview using prebuilt artifacts

Use `VERCEL_TOKEN`, `VERCEL_ORG_ID`, and `VERCEL_PROJECT_ID` repository secrets.

- [ ] **Step 2: Validate workflow syntax**

Run:

```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/vercel-preview.yml', encoding='utf-8')); print('vercel-preview workflow ok')"
```

Expected:

```text
vercel-preview workflow ok
```

## Task 4: Add Production Deployment Workflow

**Files:**
- Create: `.github/workflows/deploy-production.yml`

- [ ] **Step 1: Create production workflow**

Create `.github/workflows/deploy-production.yml` with jobs that:

- run backend and frontend gates
- call `RENDER_DEPLOY_HOOK_URL`
- poll `BACKEND_HEALTH_URL`
- deploy Vercel Production using prebuilt artifacts
- smoke check `FRONTEND_PRODUCTION_URL`
- publish URLs in the GitHub step summary

Use GitHub environment `production`.

- [ ] **Step 2: Validate workflow syntax**

Run:

```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/deploy-production.yml', encoding='utf-8')); print('production workflow ok')"
```

Expected:

```text
production workflow ok
```

## Task 5: Add CI/CD Runbook

**Files:**
- Create: `docs/vercel-render-cicd.md`
- Modify: `docs/README.md`
- Modify: `docs/release-checklist.md`
- Modify: `README.md`

- [ ] **Step 1: Create runbook**

Create `docs/vercel-render-cicd.md` covering:

- architecture
- required GitHub secrets
- Vercel environment variables
- Render environment variables
- setup steps
- PR preview behavior
- production deploy behavior
- rollback
- troubleshooting

- [ ] **Step 2: Link runbook from docs index**

Add `docs/vercel-render-cicd.md` to the Documentation Map in `docs/README.md`.

- [ ] **Step 3: Add release checklist entries**

Update `docs/release-checklist.md` so deployment readiness includes Vercel and Render secrets, backend health URL, frontend URL, and rollback readiness.

- [ ] **Step 4: Link runbook from root README**

Add the runbook link to the Documentation section in `README.md`.

## Task 6: Local Verification

**Files:**
- Read: all files modified above

- [ ] **Step 1: Check working tree**

Run:

```bash
git status --short
```

Expected: only the planned files are modified or added.

- [ ] **Step 2: Validate config syntax**

Run:

```bash
node -e "JSON.parse(require('fs').readFileSync('vercel.json', 'utf8')); console.log('vercel.json ok')"
python3 -c "import yaml; [yaml.safe_load(open(path, encoding='utf-8')) for path in ['render.yaml','.github/workflows/vercel-preview.yml','.github/workflows/deploy-production.yml','backend/src/main/resources/application-prod.yml']]; print('yaml ok')"
```

Expected:

```text
vercel.json ok
yaml ok
```

- [ ] **Step 3: Run frontend checks**

Run:

```bash
pnpm install --frozen-lockfile
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```

Expected: all commands exit with code 0.

- [ ] **Step 4: Run backend checks**

Run:

```bash
(cd backend && ./mvnw -B -ntp verify)
```

Expected: Maven exits with code 0.

- [ ] **Step 5: Commit implementation**

Run:

```bash
git add vercel.json render.yaml backend/Dockerfile .github/workflows/vercel-preview.yml .github/workflows/deploy-production.yml docs/vercel-render-cicd.md docs/README.md docs/release-checklist.md README.md docs/superpowers/plans/2026-05-31-enterprise-free-ci-cd-implementation-plan.md
git commit -m "ci: add vercel render production deployment"
```
