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

4. Recommended Phase 1 routes to verify:

- `/`
- `/collections/women`
- `/collections/men`
- `/products/everyday-move`
- `/auth/login`
- `/auth/register`
- `/checkout`
- `/account/orders`

The buyer shell now includes:

- current-user bootstrap and signed-in header state
- collection and product-detail routes backed by the catalog API
- cart drawer and checkout shell
- protected buyer order history and order detail screens

## Build and Lint

```powershell
pnpm --dir frontend lint
pnpm --dir frontend test:coverage
pnpm --dir frontend build
```
