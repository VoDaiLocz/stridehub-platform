insert into categories (id, name, slug, description, is_active)
values
    ('40000000-0000-0000-0000-000000000001', 'Women', 'women', 'Everyday weather-ready movement for women.', true),
    ('40000000-0000-0000-0000-000000000002', 'Men', 'men', 'Waterproof staples for men.', true),
    ('40000000-0000-0000-0000-000000000003', 'Outdoor', 'outdoor', 'Traction-forward silhouettes for wet commutes and trails.', true),
    ('40000000-0000-0000-0000-000000000004', 'Kids', 'kids', 'Lightweight waterproof pairs for younger shoppers.', true)
on conflict (id) do nothing;

insert into brands (id, name, slug, description, is_active)
values
    ('41000000-0000-0000-0000-000000000001', 'StrideHub Studio', 'stridehub-studio', 'Core StrideHub in-house line.', true),
    ('41000000-0000-0000-0000-000000000002', 'Motion Foundry', 'motion-foundry', 'Performance-led seasonal drops.', true)
on conflict (id) do nothing;

insert into products (
    id, seller_id, category_id, brand_id, name, slug, short_description, long_description, currency_code, status, published_at, version
)
values
    (
        '42000000-0000-0000-0000-000000000001',
        null,
        '40000000-0000-0000-0000-000000000001',
        '41000000-0000-0000-0000-000000000001',
        'Everyday Move',
        'everyday-move',
        'Premium knit runner built for rainy city loops.',
        'Everyday Move is the hero Phase 1 product for women shoppers. It mixes lightweight cushioning, waterproof protection, and a clean lifestyle silhouette.',
        'USD',
        'ACTIVE',
        current_timestamp,
        0
    ),
    (
        '42000000-0000-0000-0000-000000000002',
        null,
        '40000000-0000-0000-0000-000000000002',
        '41000000-0000-0000-0000-000000000001',
        'Weekend Sneaker',
        'weekend-sneaker',
        'Easy everyday pair with weatherproof comfort.',
        'Weekend Sneaker covers the men collection route and gives the storefront a second live PDP with multiple variants.',
        'USD',
        'ACTIVE',
        current_timestamp,
        0
    ),
    (
        '42000000-0000-0000-0000-000000000003',
        null,
        '40000000-0000-0000-0000-000000000003',
        '41000000-0000-0000-0000-000000000002',
        'Stormside Boot',
        'stormside-boot',
        'High-top waterproof boot for rough weather.',
        'Stormside Boot supports the outdoor collection route with a darker, utility-led silhouette and stronger traction story.',
        'USD',
        'ACTIVE',
        current_timestamp,
        0
    ),
    (
        '42000000-0000-0000-0000-000000000004',
        null,
        '40000000-0000-0000-0000-000000000004',
        '41000000-0000-0000-0000-000000000002',
        'Sunday Slipper',
        'sunday-slipper',
        'Soft knit slip-on with waterproof shell.',
        'Sunday Slipper gives the kids collection a light, comfort-led shape for empty-state-free manual demos.',
        'USD',
        'ACTIVE',
        current_timestamp,
        0
    )
on conflict (id) do nothing;

insert into product_variants (
    id, product_id, sku, size_value, color_value, price_amount, compare_at_amount, status, version
)
values
    ('43000000-0000-0000-0000-000000000001', '42000000-0000-0000-0000-000000000001', 'EVM-SLATE-38', '38', 'Slate', 135.00, 155.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000002', '42000000-0000-0000-0000-000000000001', 'EVM-FOAM-39', '39', 'Sea Foam', 135.00, 155.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000003', '42000000-0000-0000-0000-000000000002', 'WKN-IVORY-42', '42', 'Ivory', 110.00, 130.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000004', '42000000-0000-0000-0000-000000000002', 'WKN-BLACK-43', '43', 'Onyx', 110.00, 130.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000005', '42000000-0000-0000-0000-000000000003', 'STB-EMBER-43', '43', 'Ember', 165.00, 189.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000006', '42000000-0000-0000-0000-000000000003', 'STB-MOSS-44', '44', 'Moss', 165.00, 189.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000007', '42000000-0000-0000-0000-000000000004', 'SUN-SAND-35', '35', 'Sand', 85.00, 99.00, 'ACTIVE', 0),
    ('43000000-0000-0000-0000-000000000008', '42000000-0000-0000-0000-000000000004', 'SUN-CLOUD-36', '36', 'Cloud', 85.00, 99.00, 'ACTIVE', 0)
on conflict (id) do nothing;

insert into product_images (id, product_id, variant_id, image_url, alt_text, sort_order)
values
    ('44000000-0000-0000-0000-000000000001', '42000000-0000-0000-0000-000000000001', null, '/images/vessi.com/shoe-1.jpg', 'Everyday Move hero image', 0),
    ('44000000-0000-0000-0000-000000000002', '42000000-0000-0000-0000-000000000001', '43000000-0000-0000-0000-000000000001', '/images/feature-1.jpg', 'Everyday Move editorial detail', 1),
    ('44000000-0000-0000-0000-000000000003', '42000000-0000-0000-0000-000000000002', null, '/images/vessi.com/shoe-2.jpg', 'Weekend Sneaker hero image', 0),
    ('44000000-0000-0000-0000-000000000004', '42000000-0000-0000-0000-000000000002', '43000000-0000-0000-0000-000000000004', '/images/feature-2.jpg', 'Weekend Sneaker editorial detail', 1),
    ('44000000-0000-0000-0000-000000000005', '42000000-0000-0000-0000-000000000003', null, '/images/vessi.com/shoe-3.jpg', 'Stormside Boot hero image', 0),
    ('44000000-0000-0000-0000-000000000006', '42000000-0000-0000-0000-000000000003', '43000000-0000-0000-0000-000000000006', '/images/feature-3.jpg', 'Stormside Boot editorial detail', 1),
    ('44000000-0000-0000-0000-000000000007', '42000000-0000-0000-0000-000000000004', null, '/images/vessi.com/shoe-4.jpg', 'Sunday Slipper hero image', 0),
    ('44000000-0000-0000-0000-000000000008', '42000000-0000-0000-0000-000000000004', '43000000-0000-0000-0000-000000000008', '/images/hero-banner.webp', 'Sunday Slipper editorial detail', 1)
on conflict (id) do nothing;

insert into inventory_items (id, variant_id, available_quantity, reserved_quantity, version)
values
    ('45000000-0000-0000-0000-000000000001', '43000000-0000-0000-0000-000000000001', 18, 0, 0),
    ('45000000-0000-0000-0000-000000000002', '43000000-0000-0000-0000-000000000002', 9, 0, 0),
    ('45000000-0000-0000-0000-000000000003', '43000000-0000-0000-0000-000000000003', 12, 0, 0),
    ('45000000-0000-0000-0000-000000000004', '43000000-0000-0000-0000-000000000004', 6, 0, 0),
    ('45000000-0000-0000-0000-000000000005', '43000000-0000-0000-0000-000000000005', 7, 0, 0),
    ('45000000-0000-0000-0000-000000000006', '43000000-0000-0000-0000-000000000006', 4, 0, 0),
    ('45000000-0000-0000-0000-000000000007', '43000000-0000-0000-0000-000000000007', 10, 0, 0),
    ('45000000-0000-0000-0000-000000000008', '43000000-0000-0000-0000-000000000008', 8, 0, 0)
on conflict (id) do nothing;
