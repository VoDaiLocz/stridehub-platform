# Enterprise Free-Tier CI/CD Design

## Context

StrideHub is a monorepo with a Spring Boot backend in `backend/` and a Vite React frontend in `frontend/`. The repository already has CI workflows for backend verification, frontend verification, dependency review, pull-request governance, and release artifact packaging.

The next step is to add a professional production deployment flow while staying on free or near-free infrastructure.

## Selected Platform

- Source control and pipeline: GitHub Actions
- Frontend hosting: Vercel Free
- Backend hosting: Render Free
- PostgreSQL: Neon Free or Supabase Free
- Redis, if required: Upstash Free

This keeps operational cost low while preserving production-style delivery controls: pull-request gates, preview environments, production deployment gates, health checks, and rollback documentation.

## Delivery Flow

### Pull Request Flow

Every pull request into `main` should run:

- pull-request governance validation
- dependency review
- backend Maven verification
- frontend install, lint, test coverage, and production build
- Vercel Preview deployment for pull requests from the same repository

Forked pull requests should still run quality gates, but preview deployment must be skipped because GitHub does not expose repository secrets to forked pull requests.

### Main Branch Production Flow

Every push to `main` should run:

- full backend and frontend quality gates
- Render backend deployment through a deploy hook
- backend production health check against `/actuator/health`
- Vercel frontend production deployment
- production smoke check for the deployed frontend URL

The backend deploy must happen before the frontend production deploy because the frontend depends on `VITE_API_BASE_URL`.

## Workflow Design

### CI Workflow

The existing CI workflow remains the required branch-protection gate. It should continue to verify backend and frontend correctness independently from deployment.

Expected required checks:

- `Backend Verification`
- `Frontend Verification`
- `Quality Gate`
- dependency review for dependency changes
- pull-request governance for PR discipline

### Preview Deployment Workflow

A dedicated preview workflow deploys only the frontend to Vercel Preview after CI-style gates pass. It uses Vercel CLI with prebuilt artifacts so GitHub Actions owns the build and logs.

Required behavior:

- trigger on pull requests into `main`
- run backend and frontend deployment gates
- skip deployment for forked pull requests
- publish the preview URL in the workflow summary
- use GitHub environment `vercel-preview`

### Production Deployment Workflow

A dedicated production workflow controls release to Render and Vercel.

Required behavior:

- trigger on push to `main`
- support manual dispatch
- run backend and frontend gates before deployment
- call `RENDER_DEPLOY_HOOK_URL`
- poll backend health until healthy or timeout
- deploy Vercel production using CLI prebuilt flow
- publish backend and frontend URLs in the workflow summary
- use GitHub environment `production`
- recommend required reviewer approval on `production`

## Configuration Files

### `vercel.json`

The repository root should define Vercel monorepo behavior:

- install command: `pnpm install --frozen-lockfile`
- build command: `pnpm --dir frontend build`
- output directory: `frontend/dist`
- SPA fallback rewrite to `/index.html`

### `render.yaml`

The repository should include a Render blueprint for the backend service:

- service type: web service
- runtime: Docker or native Java build
- root directory: `backend`
- build command: `./mvnw -B -ntp -DskipTests package`
- start command: `java -jar target/*.jar`
- health check path: `/actuator/health`

Render database resources can be documented but should not be assumed in the blueprint if using Neon or Supabase externally.

## Secrets And Environment Variables

GitHub repository secrets:

- `VERCEL_TOKEN`
- `VERCEL_ORG_ID`
- `VERCEL_PROJECT_ID`
- `RENDER_DEPLOY_HOOK_URL`
- `BACKEND_HEALTH_URL`
- `FRONTEND_PRODUCTION_URL`

Vercel project environment variables:

- `VITE_API_BASE_URL` for Preview
- `VITE_API_BASE_URL` for Production

Render backend environment variables:

- database connection settings or `DATABASE_URL`, depending on the backend configuration
- JWT secret
- Redis settings if Redis is enabled
- mail settings if mail integration is enabled

## Verification

Local verification before committing CI/CD changes:

- `./backend/mvnw -B -ntp verify`
- `pnpm install --frozen-lockfile`
- `pnpm --dir frontend lint`
- `pnpm --dir frontend test:coverage`
- `pnpm --dir frontend build`

Pipeline verification after pushing:

- open a pull request and confirm preview deployment
- merge to `main` and confirm backend deploy, backend health check, frontend production deploy, and smoke check

## Rollback

Frontend rollback:

- redeploy the previous known-good Vercel deployment, or
- revert the bad commit on `main` and let the production workflow deploy the corrected state

Backend rollback:

- redeploy the previous Render deployment from the Render dashboard when speed matters
- otherwise revert the bad commit and trigger the production workflow

Rollback should be followed by a production health check and a short incident note in the relevant issue or release notes.

## Non-Goals

- No paid infrastructure requirement.
- No Kubernetes or container registry requirement for the first production setup.
- No database migration automation beyond backend startup/Flyway behavior already present in the application.
- No automatic deployment from Vercel or Render dashboards bypassing GitHub Actions as the source of deployment control.
