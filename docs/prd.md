# StrideHub Product Requirements Document

## 1. Product Overview

StrideHub la nen tang marketplace nhieu nguoi ban, tap trung vao footwear va cac bien the san pham nhu `size`, `color`, `sku`, `inventory`. Buyer-facing experience duoc thiet ke theo huong retail premium, nhung phan cot loi cua san pham la backend van hanh don hang, thanh toan, ton kho, seller governance va auditability.

San pham khong dat muc tieu tro thanh san thuong mai dien tu tong quat o giai doan dau. Muc tieu la tao mot he thong co flow thuc te va co do sau ky thuat de mo rong thanh production-style platform.

## 2. Business Goals

- Xay dung MVP co kha nang xu ly don va thanh toan cho marketplace footwear.
- Dam bao khong oversell o case canh tranh co ban.
- Ho tro onboarding seller va moderation de quan ly chat luong catalog.
- Tao nen tang cho refund, reconciliation va support operation o phase tiep theo.

## 3. Success Metrics

### Product Metrics

- Checkout completion rate
- Order confirmation success rate
- Catalog conversion by product detail view
- Seller approval turnaround time
- Refund turnaround time

### Engineering Metrics

- Zero duplicate order confirmation tu webhook retry
- Zero inventory commit duplicate tren cung mot reservation
- p95 catalog read < 300ms
- p95 checkout validation < 500ms

## 4. Primary Personas

### Buyer

- Duyet san pham theo category va brand
- Xem variant theo size/color
- Them vao cart
- Checkout va thanh toan
- Theo doi order, cancel, request return

### Seller

- Nop ho so dang ky
- Quan ly product, SKU, inventory
- Xac nhan fulfill don
- Xu ly refund/request theo policy

### Admin / Ops

- Duyet seller application
- Duyet/tu choi product
- Can thiep order bat thuong
- Tim kiem audit va theo doi van hanh

## 5. In Scope V1

- Auth voi JWT access token va refresh token
- Catalog: category, brand, product, variant, product image
- Inventory theo SKU
- Cart va checkout
- External payment gateway pattern
- Payment webhook + idempotent processing
- Order lifecycle co state machine
- Seller application va approval
- Admin moderation co audit log

## 6. Out of Scope V1

- Seller payout orchestration
- Recommendation engine
- Dynamic pricing engine
- Fraud scoring
- Multi-warehouse inventory routing
- Search engine rieng

## 7. Key User Journeys

### Buyer Purchase Journey

1. Dang ky hoac dang nhap
2. Duyet product listing
3. Chon variant `size/color`
4. Them vao cart
5. Tao checkout session
6. Thanh toan qua gateway
7. Nhan xac nhan don

### Seller Onboarding Journey

1. Dang ky account
2. Gui seller application
3. Admin review
4. Approval
5. Tao product va SKU
6. Product duoc moderation
7. Product active va bat dau nhan order

## 8. Risks and Constraints

- Webhook tu payment provider co the gui lap lai hoac den tre.
- Inventory reservation neu sai se gay oversell.
- Product variant la don vi ton kho that, nen schema va flow phai uu tien SKU-first.
- Hien tai project duoc thiet ke de build trong mot codebase, nen can giu boundary ro rang de tranh "big ball of mud".
