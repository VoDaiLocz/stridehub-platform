create table seller_profiles (
    id uuid primary key,
    user_id uuid not null unique,
    display_name varchar(255) not null,
    legal_name varchar(255),
    status varchar(32) not null,
    approved_at timestamp with time zone,
    suspended_at timestamp with time zone,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_seller_profiles_user foreign key (user_id) references users (id)
);

create table seller_applications (
    id uuid primary key,
    user_id uuid not null unique,
    store_name varchar(255) not null,
    legal_name varchar(255),
    status varchar(32) not null,
    submitted_at timestamp with time zone not null,
    reviewed_at timestamp with time zone,
    reviewed_by uuid,
    rejection_reason varchar(255),
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_seller_applications_user foreign key (user_id) references users (id),
    constraint fk_seller_applications_reviewer foreign key (reviewed_by) references users (id)
);

alter table products
    add constraint fk_products_seller foreign key (seller_id) references seller_profiles (id);

alter table orders
    add constraint fk_orders_seller foreign key (seller_id) references seller_profiles (id);

create table refunds (
    id uuid primary key,
    payment_id uuid not null,
    order_id uuid not null,
    refund_reference varchar(128) not null unique,
    status varchar(32) not null,
    amount numeric(12, 2) not null,
    reason varchar(255),
    processed_at timestamp with time zone,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_refunds_payment foreign key (payment_id) references payments (id),
    constraint fk_refunds_order foreign key (order_id) references orders (id)
);

create table reviews (
    id uuid primary key,
    order_item_id uuid not null,
    product_id uuid not null,
    buyer_id uuid not null,
    rating integer not null,
    title varchar(255),
    body text,
    status varchar(32) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_reviews_order_item foreign key (order_item_id) references order_items (id),
    constraint fk_reviews_product foreign key (product_id) references products (id),
    constraint fk_reviews_buyer foreign key (buyer_id) references users (id),
    constraint uq_reviews_order_item_buyer unique (order_item_id, buyer_id),
    constraint chk_reviews_rating check (rating between 1 and 5)
);

create table wishlist_items (
    id uuid primary key,
    user_id uuid not null,
    product_id uuid not null,
    created_at timestamp with time zone not null default current_timestamp,
    constraint fk_wishlist_items_user foreign key (user_id) references users (id),
    constraint fk_wishlist_items_product foreign key (product_id) references products (id),
    constraint uq_wishlist_user_product unique (user_id, product_id)
);

create table notifications (
    id uuid primary key,
    user_id uuid not null,
    type varchar(64) not null,
    title varchar(255) not null,
    body varchar(1000),
    status varchar(32) not null,
    sent_at timestamp with time zone,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_notifications_user foreign key (user_id) references users (id)
);

create table audit_logs (
    id uuid primary key,
    actor_user_id uuid,
    actor_type varchar(32) not null,
    action varchar(128) not null,
    entity_type varchar(128) not null,
    entity_id uuid,
    correlation_id varchar(64) not null,
    reason varchar(255),
    metadata text,
    created_at timestamp with time zone not null default current_timestamp,
    constraint fk_audit_logs_actor_user foreign key (actor_user_id) references users (id)
);

create table outbox_events (
    id uuid primary key,
    aggregate_type varchar(128) not null,
    aggregate_id uuid not null,
    event_type varchar(128) not null,
    payload text not null,
    status varchar(32) not null,
    available_at timestamp with time zone not null,
    published_at timestamp with time zone,
    retry_count integer not null default 0,
    correlation_id varchar(64),
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp
);

create index idx_seller_profiles_status on seller_profiles (status);
create index idx_seller_applications_status on seller_applications (status);
create index idx_audit_logs_correlation_id on audit_logs (correlation_id);
create index idx_outbox_events_status_available_at on outbox_events (status, available_at);
create index idx_reviews_product_status on reviews (product_id, status);
