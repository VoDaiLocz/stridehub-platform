# StrideHub Buyer Web

Buyer-facing frontend workspace for the StrideHub marketplace platform.

## Stack

- React 19
- TypeScript
- Vite
- ESLint

## Local Development

1. Copy `.env.example` to `.env` if you need to override the API base URL.
2. Install workspace dependencies from the repository root:

   ```powershell
   pnpm install
   ```

3. Start the frontend:

   ```powershell
   pnpm --dir frontend dev
   ```

## Build and Lint

```powershell
pnpm --dir frontend lint
pnpm --dir frontend build
```
