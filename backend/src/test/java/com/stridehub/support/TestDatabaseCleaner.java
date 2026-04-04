package com.stridehub.support;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public final class TestDatabaseCleaner {

    private static final List<String> DELETE_STATEMENTS = List.of(
            "delete from inventory_reservations",
            "delete from refunds",
            "delete from reviews",
            "delete from wishlist_items",
            "delete from notifications",
            "delete from audit_logs",
            "delete from outbox_events",
            "delete from order_items",
            "delete from shipments",
            "delete from orders",
            "delete from payment_attempts",
            "delete from payments",
            "delete from checkout_sessions",
            "delete from cart_items",
            "delete from carts",
            "delete from inventory_items",
            "delete from product_images",
            "delete from product_variants",
            "delete from products",
            "delete from categories",
            "delete from brands",
            "delete from seller_profiles",
            "delete from seller_applications",
            "delete from refresh_tokens",
            "delete from addresses",
            "delete from user_roles",
            "delete from users"
    );

    private TestDatabaseCleaner() {
    }

    public static void reset(JdbcTemplate jdbcTemplate) {
        DELETE_STATEMENTS.forEach(jdbcTemplate::execute);
    }
}
