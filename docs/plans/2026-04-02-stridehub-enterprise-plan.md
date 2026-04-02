# StrideHub Enterprise Implementation Plan

> Design baseline for a modular-monolith, production-style marketplace backend.

## Summary

- Domain: `multi-vendor footwear marketplace`
- Architecture: `modular monolith`
- Runtime stack: `Java 21`, `Spring Boot`, `PostgreSQL`, `Redis`, `Flyway`
- Integration style: `REST + domain events + outbox`
- Buyer UI inspiration: `Vessi`
- Payment strategy: external gateway pattern (`Stripe/Momo/VNPay` style)

## Core Modules

- Identity & Access
- Seller Management
- Catalog
- Inventory
- Cart
- Checkout
- Payment
- Order Management
- Returns & Refunds
- Reviews & Ratings
- Notifications
- Admin & Moderation
- Reporting & Audit

## Architectural Principles

- Single transaction chi ton tai trong mot bounded context.
- Payment webhook chi di qua payment boundary va publish event thay vi mutate nhieu module truc tiep.
- Moi hanh dong quan trong phai co `idempotency key`, `correlation id`, `audit log`.
- Product variant la source of truth cho pricing va inventory.
- Cart phai duoc revalidate tai checkout.

## Phase Plan

### Phase 1

- Auth
- Catalog
- Variant inventory
- Cart
- Checkout
- Payment webhook
- Order confirmation
- Admin bootstrap

### Phase 2

- Seller onboarding
- Product moderation
- Seller inventory console
- Seller fulfillment
- Reviews

### Phase 3

- Refunds
- Reconciliation
- Support tooling
- Dashboards
- Alerts
- Audit search

### Phase 4

- Search service
- Recommendations
- Payout orchestration
- Fraud/risk scoring
- Multi-warehouse support

## Key Contracts

- `Payment webhook` la source xac nhan thanh toan cuoi cung.
- `InventoryReservation` phai duoc `ACTIVE -> COMMITTED|RELEASED|EXPIRED`.
- `Order placement` va `refund processing` phai idempotent.
- `Admin action` luon co actor, timestamp, reason.

## Acceptance Baseline

- Khong duplicate order khi webhook retry
- Khong oversell o case canh tranh co ban
- Co trace va audit cho order/payment/refund
- Role boundary ro giua buyer/seller/admin
