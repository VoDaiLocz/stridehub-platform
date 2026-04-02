create table users (
    id uuid primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    status varchar(32) not null,
    email_verified boolean not null default false,
    first_name varchar(100),
    last_name varchar(100),
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp
);

create table roles (
    id uuid primary key,
    code varchar(64) not null unique,
    description varchar(255),
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp
);

create table user_roles (
    user_id uuid not null,
    role_id uuid not null,
    created_at timestamp with time zone not null default current_timestamp,
    primary key (user_id, role_id),
    constraint fk_user_roles_user foreign key (user_id) references users (id),
    constraint fk_user_roles_role foreign key (role_id) references roles (id)
);

create table refresh_tokens (
    id uuid primary key,
    user_id uuid not null,
    token_hash varchar(255) not null unique,
    expires_at timestamp with time zone not null,
    revoked_at timestamp with time zone,
    replaced_by_token_id uuid,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_refresh_tokens_user foreign key (user_id) references users (id)
);

create table addresses (
    id uuid primary key,
    user_id uuid not null,
    label varchar(100),
    recipient_name varchar(150) not null,
    phone_number varchar(50),
    line1 varchar(255) not null,
    line2 varchar(255),
    city varchar(120) not null,
    state varchar(120),
    postal_code varchar(50) not null,
    country_code char(2) not null,
    is_default boolean not null default false,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_addresses_user foreign key (user_id) references users (id)
);

create table categories (
    id uuid primary key,
    name varchar(150) not null,
    slug varchar(160) not null unique,
    description varchar(500),
    is_active boolean not null default true,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp
);

create table brands (
    id uuid primary key,
    name varchar(150) not null,
    slug varchar(160) not null unique,
    description varchar(500),
    is_active boolean not null default true,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp
);

create table products (
    id uuid primary key,
    seller_id uuid,
    category_id uuid not null,
    brand_id uuid not null,
    name varchar(255) not null,
    slug varchar(255) not null unique,
    short_description varchar(500),
    long_description text,
    currency_code char(3) not null,
    status varchar(32) not null,
    published_at timestamp with time zone,
    version bigint not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_products_category foreign key (category_id) references categories (id),
    constraint fk_products_brand foreign key (brand_id) references brands (id)
);

create table product_variants (
    id uuid primary key,
    product_id uuid not null,
    sku varchar(64) not null unique,
    size_value varchar(32) not null,
    color_value varchar(32) not null,
    price_amount numeric(12, 2) not null,
    compare_at_amount numeric(12, 2),
    status varchar(32) not null,
    version bigint not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_product_variants_product foreign key (product_id) references products (id)
);

create table product_images (
    id uuid primary key,
    product_id uuid not null,
    variant_id uuid,
    image_url varchar(1024) not null,
    alt_text varchar(255),
    sort_order integer not null default 0,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint fk_product_images_product foreign key (product_id) references products (id),
    constraint fk_product_images_variant foreign key (variant_id) references product_variants (id)
);

create index idx_products_status_published_at on products (status, published_at);
create index idx_products_category_status on products (category_id, status);
create index idx_products_brand_status on products (brand_id, status);
create index idx_product_variants_product_status on product_variants (product_id, status);

insert into roles (id, code, description)
values
    ('11111111-1111-1111-1111-111111111111', 'BUYER', 'Default buyer role'),
    ('22222222-2222-2222-2222-222222222222', 'SELLER', 'Seller role'),
    ('33333333-3333-3333-3333-333333333333', 'ADMIN', 'Administrator role');
