create table inventory_items (
    id uuid primary key,
    variant_id uuid not null unique,
    available_quantity integer not null,
    reserved_quantity integer not null default 0,
    version bigint not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_inventory_items_variant foreign key (variant_id) references product_variants (id),
    constraint chk_inventory_items_available_quantity check (available_quantity >= 0),
    constraint chk_inventory_items_reserved_quantity check (reserved_quantity >= 0)
);

create table carts (
    id uuid primary key,
    user_id uuid not null unique,
    status varchar(32) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_carts_user foreign key (user_id) references users (id)
);

create table cart_items (
    id uuid primary key,
    cart_id uuid not null,
    variant_id uuid not null,
    quantity integer not null,
    unit_price_amount numeric(12, 2),
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_cart_items_cart foreign key (cart_id) references carts (id),
    constraint fk_cart_items_variant foreign key (variant_id) references product_variants (id),
    constraint uq_cart_variant unique (cart_id, variant_id),
    constraint chk_cart_items_quantity check (quantity > 0)
);

create table checkout_sessions (
    id uuid primary key,
    user_id uuid not null,
    cart_id uuid not null unique,
    status varchar(32) not null,
    currency_code char(3) not null,
    subtotal_amount numeric(12, 2) not null,
    discount_amount numeric(12, 2) not null default 0,
    shipping_amount numeric(12, 2) not null default 0,
    total_amount numeric(12, 2) not null,
    expires_at timestamp with time zone not null,
    idempotency_key varchar(128) not null unique,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_checkout_sessions_user foreign key (user_id) references users (id),
    constraint fk_checkout_sessions_cart foreign key (cart_id) references carts (id)
);

create table payments (
    id uuid primary key,
    checkout_session_id uuid not null unique,
    provider varchar(32) not null,
    provider_payment_ref varchar(128) not null unique,
    status varchar(32) not null,
    currency_code char(3) not null,
    amount numeric(12, 2) not null,
    idempotency_key varchar(128) not null unique,
    confirmed_at timestamp with time zone,
    failure_reason varchar(255),
    version bigint not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_payments_checkout_session foreign key (checkout_session_id) references checkout_sessions (id)
);

create table payment_attempts (
    id uuid primary key,
    payment_id uuid not null,
    provider_status varchar(64) not null,
    provider_request_id varchar(128),
    raw_reference varchar(255),
    attempted_at timestamp with time zone not null,
    success boolean not null default false,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_payment_attempts_payment foreign key (payment_id) references payments (id)
);

create table orders (
    id uuid primary key,
    user_id uuid not null,
    seller_id uuid,
    payment_id uuid not null unique,
    checkout_session_id uuid not null unique,
    order_number varchar(64) not null unique,
    status varchar(32) not null,
    currency_code char(3) not null,
    subtotal_amount numeric(12, 2) not null,
    discount_amount numeric(12, 2) not null default 0,
    shipping_amount numeric(12, 2) not null default 0,
    total_amount numeric(12, 2) not null,
    placed_at timestamp with time zone,
    version bigint not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_orders_user foreign key (user_id) references users (id),
    constraint fk_orders_payment foreign key (payment_id) references payments (id),
    constraint fk_orders_checkout_session foreign key (checkout_session_id) references checkout_sessions (id)
);

create table order_items (
    id uuid primary key,
    order_id uuid not null,
    product_id uuid not null,
    variant_id uuid not null,
    sku varchar(64) not null,
    product_name varchar(255) not null,
    variant_name varchar(255) not null,
    quantity integer not null,
    unit_price_amount numeric(12, 2) not null,
    total_price_amount numeric(12, 2) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_order_items_order foreign key (order_id) references orders (id),
    constraint fk_order_items_product foreign key (product_id) references products (id),
    constraint fk_order_items_variant foreign key (variant_id) references product_variants (id),
    constraint chk_order_items_quantity check (quantity > 0)
);

create table shipments (
    id uuid primary key,
    order_id uuid not null unique,
    status varchar(32) not null,
    carrier varchar(100),
    tracking_number varchar(100),
    shipped_at timestamp with time zone,
    delivered_at timestamp with time zone,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_shipments_order foreign key (order_id) references orders (id)
);

create table inventory_reservations (
    id uuid primary key,
    variant_id uuid not null,
    checkout_session_id uuid,
    order_id uuid,
    quantity integer not null,
    status varchar(32) not null,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_inventory_reservations_variant foreign key (variant_id) references product_variants (id),
    constraint fk_inventory_reservations_checkout_session foreign key (checkout_session_id) references checkout_sessions (id),
    constraint fk_inventory_reservations_order foreign key (order_id) references orders (id),
    constraint chk_inventory_reservations_quantity check (quantity > 0)
);

create index idx_inventory_reservations_status_expires_at on inventory_reservations (status, expires_at);
create index idx_orders_created_at on orders (created_at);
create index idx_orders_status_created_at on orders (status, created_at);
create index idx_order_items_order_id on order_items (order_id);
create index idx_payments_status_created_at on payments (status, created_at);
