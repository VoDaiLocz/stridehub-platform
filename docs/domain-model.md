# StrideHub Domain Model

## 1. Domain Boundaries

StrideHub uses a domain model centered around commerce correctness and marketplace governance. The major business areas are:

- identity and access
- seller governance
- catalog
- inventory
- cart and checkout
- payments
- orders
- refunds
- audit and operations

## 2. Aggregate View

### User

Represents an authenticated platform identity. A user may become a seller through an application process and may own multiple addresses, refresh tokens, wishlist items, and orders.

### SellerProfile

Extends a user with seller-specific capabilities and status. Seller lifecycle is governed by approval and suspension rules.

### Product

Represents the merchandising entity visible to buyers. A product contains presentation-level metadata, publication status, and one or more sellable variants.

### ProductVariant

Represents the actual purchasable unit. This is the key business object for size, color, price selection, and stock control. Variant identity is anchored by SKU.

### InventoryItem

Represents available stock for a specific variant. Inventory changes are mediated by reservation and commit/release actions rather than naive decrements.

### Cart

Represents a buyer's pre-checkout basket. Cart data is advisory until checkout validation succeeds.

### CheckoutSession

Represents a structured attempt to transform a validated cart into a payment and, later, a confirmed order.

### Payment

Tracks a provider-backed payment lifecycle and all attempts associated with that lifecycle.

### Order

Represents the post-payment commercial commitment and becomes the central record for fulfillment, cancellation, and refund coordination.

## 3. Entity Relationship Diagram

```mermaid
erDiagram
    USER ||--o{ ADDRESS : has
    USER ||--o{ REFRESH_TOKEN : owns
    USER ||--o{ ORDER : places
    USER ||--o{ REVIEW : writes
    USER ||--o{ WISHLIST_ITEM : saves
    USER ||--o| SELLER_PROFILE : may_become

    SELLER_PROFILE ||--o{ SELLER_APPLICATION : submits
    SELLER_PROFILE ||--o{ PRODUCT : publishes

    CATEGORY ||--o{ PRODUCT : categorizes
    BRAND ||--o{ PRODUCT : brands
    PRODUCT ||--o{ PRODUCT_VARIANT : has
    PRODUCT ||--o{ PRODUCT_IMAGE : has

    PRODUCT_VARIANT ||--|| INVENTORY_ITEM : stocked_as
    PRODUCT_VARIANT ||--o{ CART_ITEM : selected_in
    PRODUCT_VARIANT ||--o{ ORDER_ITEM : sold_as
    PRODUCT_VARIANT ||--o{ INVENTORY_RESERVATION : reserved_as

    USER ||--|| CART : owns
    CART ||--o{ CART_ITEM : contains

    CHECKOUT_SESSION ||--o{ INVENTORY_RESERVATION : creates
    CHECKOUT_SESSION ||--o| PAYMENT : initiates
    CHECKOUT_SESSION ||--o| ORDER : confirms

    ORDER ||--o{ ORDER_ITEM : contains
    ORDER ||--o{ REFUND : has
    ORDER ||--o| SHIPMENT : ships_as

    PAYMENT ||--o{ PAYMENT_ATTEMPT : tracks
    PAYMENT ||--o{ REFUND : funds

    AUDIT_LOG }o--|| USER : actor
    OUTBOX_EVENT }o--|| AUDIT_LOG : correlates
```

## 4. Lifecycle Models

### Seller Status

```mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> APPROVED
    PENDING --> REJECTED
    APPROVED --> SUSPENDED
    SUSPENDED --> APPROVED
```

### Product Status

```mermaid
stateDiagram-v2
    [*] --> DRAFT
    DRAFT --> PENDING_REVIEW
    PENDING_REVIEW --> ACTIVE
    PENDING_REVIEW --> REJECTED
    ACTIVE --> ARCHIVED
```

### Order Status

```mermaid
stateDiagram-v2
    [*] --> PENDING_PAYMENT
    PENDING_PAYMENT --> PAID
    PENDING_PAYMENT --> CANCELLED
    PAID --> ALLOCATED
    ALLOCATED --> PACKED
    PACKED --> SHIPPED
    SHIPPED --> DELIVERED
    PAID --> REFUND_PENDING
    REFUND_PENDING --> REFUNDED
    DELIVERED --> RETURN_REQUESTED
    RETURN_REQUESTED --> RETURNED
```

### Payment Status

```mermaid
stateDiagram-v2
    [*] --> INITIATED
    INITIATED --> REQUIRES_ACTION
    INITIATED --> AUTHORIZED
    AUTHORIZED --> CAPTURED
    INITIATED --> FAILED
    INITIATED --> CANCELLED
    CAPTURED --> REFUNDED
```

### Inventory Reservation Status

```mermaid
stateDiagram-v2
    [*] --> ACTIVE
    ACTIVE --> COMMITTED
    ACTIVE --> RELEASED
    ACTIVE --> EXPIRED
```

## 5. Invariants

- A product cannot be purchased without selecting a variant.
- A variant must have a unique SKU.
- Inventory is tracked at variant level only.
- Cart prices are not authoritative at payment time.
- Orders can be confirmed only after a valid payment event.
- Refunds must reference both an order and a payment context.

## 6. Suggested Index Strategy

- `product_variants(sku)` unique
- `inventory_items(product_variant_id)` unique
- `inventory_reservations(status, expires_at)`
- `orders(user_id, created_at desc)`
- `payments(provider_reference)` unique
- `audit_logs(correlation_id)`

## 7. Domain Modeling Notes

- `Product` should carry merchandising and discovery concerns.
- `ProductVariant` should carry purchasable properties.
- `InventoryReservation` should exist as a first-class business entity, not just a transient cache entry.
- `AuditLog` and `OutboxEvent` should be explicit persistence concerns because supportability and asynchronous integrity are design goals, not later add-ons.
