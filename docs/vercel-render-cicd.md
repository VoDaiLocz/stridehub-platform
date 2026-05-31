# Vercel and Render CI/CD Runbook

This runbook describes the free-tier production deployment path for StrideHub.

## Architecture

- GitHub Actions is the deployment controller.
- Vercel hosts the `frontend/` Vite React app.
- Render hosts the `backend/` Spring Boot API.
- Neon or Supabase should host PostgreSQL.
- Upstash Redis is optional; Redis health is disabled by default in the production profile.

Do not enable automatic production deploys in Vercel or Render for this setup. The production workflow should deploy only after quality gates pass.

## Required GitHub Secrets

Configure these in GitHub repository settings under `Settings -> Secrets and variables -> Actions`.

- `VERCEL_TOKEN`: Vercel access token.
- `VERCEL_ORG_ID`: Vercel team or user ID.
- `VERCEL_PROJECT_ID`: Vercel project ID.
- `RENDER_DEPLOY_HOOK_URL`: Render deploy hook URL for the backend service.
- `BACKEND_HEALTH_URL`: production backend health URL, for example `https://stridehub-backend.onrender.com/actuator/health`.
- `FRONTEND_PRODUCTION_URL`: optional preferred production frontend URL. If omitted, the workflow smoke-tests the Vercel deployment URL.

Recommended GitHub environments:

- `vercel-preview`: no manual approval required.
- `production`: require reviewer approval before production deployment.

Optional GitHub environment variable:

- `RENDER_STABILIZATION_SECONDS`: seconds to wait after triggering Render before polling health. Default is `90`.

## Vercel Setup

Create or import the Vercel project with repository root as the project root. The root `vercel.json` defines:

- install command: `pnpm install --frozen-lockfile`
- build command: `pnpm --dir frontend build`
- output directory: `frontend/dist`
- SPA fallback rewrite to `/index.html`

Set Vercel environment variables:

- Preview `VITE_API_BASE_URL`: staging or Render backend URL.
- Production `VITE_API_BASE_URL`: production Render backend URL.

If using GitHub Actions as the deployment controller, disable Vercel Git auto-deploys for the project to avoid duplicate deploys.

## Render Setup

Use `render.yaml` as the service blueprint or create the service manually with equivalent settings:

- service type: web service
- runtime: Docker
- plan: free
- Dockerfile path: `backend/Dockerfile`
- Docker context: `backend`
- health check path: `/actuator/health`
- auto deploy: off

Set Render environment variables:

- `SPRING_PROFILES_ACTIVE=prod`
- `STRIDEHUB_DB_URL`
- `STRIDEHUB_DB_USERNAME`
- `STRIDEHUB_DB_PASSWORD`
- `STRIDEHUB_JWT_SECRET`
- `STRIDEHUB_WEB_ALLOWED_ORIGINS`
- `STRIDEHUB_PAYMENT_WEBHOOK_SECRET`
- `STRIDEHUB_REDIS_HEALTH_ENABLED=false`

Optional Render environment variables:

- `STRIDEHUB_REDIS_HOST`
- `STRIDEHUB_REDIS_PORT`
- `STRIDEHUB_MAIL_HOST`
- `STRIDEHUB_MAIL_PORT`
- `STRIDEHUB_MAIL_USERNAME`
- `STRIDEHUB_MAIL_PASSWORD`

For `STRIDEHUB_WEB_ALLOWED_ORIGINS`, use the public frontend URL, for example:

```text
https://stridehub.vercel.app
```

## Pull Request Flow

`.github/workflows/vercel-preview.yml` runs:

- backend Maven verification
- frontend install, lint, coverage, and build
- Vercel Preview deployment

Preview deployment is skipped for forked pull requests because repository secrets are unavailable to forked PRs.

## Production Flow

`.github/workflows/deploy-production.yml` runs on push to `main` and manual dispatch:

1. Run backend and frontend production gates.
2. Trigger Render backend deployment through `RENDER_DEPLOY_HOOK_URL`.
3. Wait for Render stabilization.
4. Poll `BACKEND_HEALTH_URL`.
5. Pull Vercel production environment variables.
6. Build and deploy the Vercel production artifact.
7. Smoke-test the frontend URL.

Backend deploys first because the frontend build consumes `VITE_API_BASE_URL`.

## Required Branch Protection

Protect `main` and require these checks before merge:

- `Backend Verification`
- `Frontend Verification`
- `Quality Gate`
- `Dependency Review`
- `Pull Request Governance`
- `Backend Preview Gate`
- `Frontend Preview Gate`

Require pull request review before merge. For production safety, also require reviewer approval on the GitHub `production` environment.

## Local Verification

Run these before pushing deployment changes:

```bash
(cd backend && ./mvnw -B -ntp verify)
pnpm install --frozen-lockfile
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```

Validate deployment config syntax:

```bash
node -e "JSON.parse(require('fs').readFileSync('vercel.json', 'utf8')); console.log('vercel.json ok')"
python3 -c "import yaml; [yaml.safe_load(open(path, encoding='utf-8')) for path in ['render.yaml','.github/workflows/vercel-preview.yml','.github/workflows/deploy-production.yml','backend/src/main/resources/application-prod.yml']]; print('yaml ok')"
```

## Rollback

Frontend rollback options:

- redeploy the previous known-good Vercel deployment from the Vercel dashboard
- revert the bad commit on `main` and let the production workflow redeploy

Backend rollback options:

- redeploy the previous known-good Render deployment from the Render dashboard
- revert the bad commit on `main` and trigger the production workflow

Rollback first if the service is down, authentication is broken, checkout is blocked, or production errors are rising quickly. Debug after the service is stable.

## Troubleshooting

If Vercel deploy fails:

- check `VERCEL_TOKEN`, `VERCEL_ORG_ID`, and `VERCEL_PROJECT_ID`
- check Vercel `VITE_API_BASE_URL`
- check whether Vercel Git auto-deploys are creating duplicate deployments

If Render deploy fails:

- check `RENDER_DEPLOY_HOOK_URL`
- inspect Render build logs
- confirm `STRIDEHUB_DB_URL`, username, password, and JWT secret are set
- confirm the database allows connections from Render

If backend health fails:

- open `BACKEND_HEALTH_URL`
- inspect Render service logs
- check Flyway migration errors
- check database connectivity
- increase `RENDER_STABILIZATION_SECONDS` if Render Free cold starts are slow

If frontend loads but API calls fail:

- confirm `VITE_API_BASE_URL` in Vercel Production
- confirm `STRIDEHUB_WEB_ALLOWED_ORIGINS` in Render includes the Vercel production origin
- check browser console CORS errors
