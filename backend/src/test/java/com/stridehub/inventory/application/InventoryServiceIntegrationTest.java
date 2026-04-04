package com.stridehub.inventory.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stridehub.common.exception.ConflictException;
import com.stridehub.inventory.domain.InventoryReservation;
import com.stridehub.inventory.domain.InventoryReservationStatus;
import com.stridehub.support.TestDatabaseCleaner;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(2);
        TestDatabaseCleaner.reset(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void shouldCreateAndReleaseReservation() {
        UUID variantId = insertInventoryGraph(3);
        CheckoutSessionFixture checkoutSession = insertCheckoutSession();

        List<InventoryReservation> reservations = inventoryService.reserveForCheckout(
                checkoutSession.checkoutSessionId(),
                List.of(new InventoryReservationCommand(variantId, 2)),
                Instant.now().plusSeconds(600)
        );

        assertThat(reservations).hasSize(1);
        assertThat(countReservationsByStatus("ACTIVE")).isEqualTo(1);
        assertThat(readReservedQuantity(variantId)).isEqualTo(2);

        inventoryService.releaseCheckoutReservations(checkoutSession.checkoutSessionId());

        assertThat(readReservedQuantity(variantId)).isEqualTo(0);
        assertThat(countReservationsByStatus("RELEASED")).isEqualTo(1);
    }

    @Test
    void shouldCommitReservationIntoAvailableStock() {
        UUID variantId = insertInventoryGraph(4);
        CheckoutSessionFixture checkoutSession = insertCheckoutSession();

        inventoryService.reserveForCheckout(
                checkoutSession.checkoutSessionId(),
                List.of(new InventoryReservationCommand(variantId, 2)),
                Instant.now().plusSeconds(600)
        );
        UUID orderId = insertOrder(checkoutSession);

        inventoryService.commitCheckoutReservations(checkoutSession.checkoutSessionId(), orderId);

        assertThat(readAvailableQuantity(variantId)).isEqualTo(2);
        assertThat(readReservedQuantity(variantId)).isEqualTo(0);
        assertThat(countReservationsByStatus("COMMITTED")).isEqualTo(1);
    }

    @Test
    void shouldPreventOversellDuringConcurrentReservation() throws Exception {
        UUID variantId = insertInventoryGraph(1);
        Instant expiresAt = Instant.now().plusSeconds(600);
        CheckoutSessionFixture firstCheckoutSession = insertCheckoutSession();
        CheckoutSessionFixture secondCheckoutSession = insertCheckoutSession();

        Callable<String> firstReserve = () -> {
            try {
                inventoryService.reserveForCheckout(
                        firstCheckoutSession.checkoutSessionId(),
                        List.of(new InventoryReservationCommand(variantId, 1)),
                        expiresAt
                );
                return "SUCCESS";
            } catch (ConflictException exception) {
                return exception.getCode();
            }
        };

        Callable<String> secondReserve = () -> {
            try {
                inventoryService.reserveForCheckout(
                        secondCheckoutSession.checkoutSessionId(),
                        List.of(new InventoryReservationCommand(variantId, 1)),
                        expiresAt
                );
                return "SUCCESS";
            } catch (ConflictException exception) {
                return exception.getCode();
            }
        };

        Future<String> first = executorService.submit(firstReserve);
        Future<String> second = executorService.submit(secondReserve);

        List<String> outcomes = List.of(first.get(), second.get());
        assertThat(outcomes).contains("SUCCESS");
        assertThat(outcomes).contains("inventory.insufficient_stock");
        assertThat(readReservedQuantity(variantId)).isEqualTo(1);
        assertThat(countReservationsByStatus("ACTIVE")).isEqualTo(1);
    }

    @Test
    void shouldRejectInvalidReservationQuantity() {
        UUID variantId = insertInventoryGraph(2);

        assertThatThrownBy(() -> inventoryService.reserveForCheckout(
                UUID.randomUUID(),
                List.of(new InventoryReservationCommand(variantId, 0)),
                Instant.now().plusSeconds(600)
        )).isInstanceOf(ConflictException.class)
                .hasMessage("Reservation quantity must be positive");
    }

    private UUID insertInventoryGraph(int availableQuantity) {
        UUID categoryId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        Instant now = Instant.now();

        jdbcTemplate.update(
                "insert into categories (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                categoryId,
                "Sneakers",
                "sneakers",
                "Sneakers",
                true,
                now,
                now
        );
        jdbcTemplate.update(
                "insert into brands (id, name, slug, description, is_active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)",
                brandId,
                "StrideLab",
                "stridelab",
                "StrideLab",
                true,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into products (
                    id, seller_id, category_id, brand_id, name, slug, short_description, long_description,
                    currency_code, status, published_at, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                productId,
                null,
                categoryId,
                brandId,
                "Cloud Walker",
                "cloud-walker-" + productId,
                "Cloud Walker",
                "Cloud Walker Long",
                "USD",
                "ACTIVE",
                now,
                0L,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into product_variants (
                    id, product_id, sku, size_value, color_value, price_amount, compare_at_amount,
                    status, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                variantId,
                productId,
                "CW-" + variantId,
                "42",
                "Blue",
                new BigDecimal("145.00"),
                null,
                "ACTIVE",
                0L,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into inventory_items (
                    id, variant_id, available_quantity, reserved_quantity, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                inventoryId,
                variantId,
                availableQuantity,
                0,
                0L,
                now,
                now
        );
        return variantId;
    }

    private CheckoutSessionFixture insertCheckoutSession() {
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID checkoutSessionId = UUID.randomUUID();
        Instant now = Instant.now();
        jdbcTemplate.update(
                """
                insert into users (
                    id, email, password_hash, status, email_verified, first_name, last_name, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                "buyer-" + userId + "@example.com",
                "hashed-password",
                "ACTIVE",
                false,
                "Buyer",
                "One",
                now,
                now
        );
        jdbcTemplate.update(
                "insert into carts (id, user_id, status, created_at, updated_at) values (?, ?, ?, ?, ?)",
                cartId,
                userId,
                "ACTIVE",
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into checkout_sessions (
                    id, user_id, cart_id, status, currency_code, subtotal_amount, discount_amount,
                    shipping_amount, total_amount, expires_at, idempotency_key, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                checkoutSessionId,
                userId,
                cartId,
                "PENDING_PAYMENT",
                "USD",
                new BigDecimal("145.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("145.00"),
                now.plusSeconds(600),
                "checkout-" + checkoutSessionId,
                now,
                now
        );
        return new CheckoutSessionFixture(userId, cartId, checkoutSessionId);
    }

    private UUID insertOrder(CheckoutSessionFixture checkoutSession) {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Instant now = Instant.now();
        jdbcTemplate.update(
                """
                insert into payments (
                    id, checkout_session_id, provider, provider_payment_ref, status, currency_code, amount,
                    idempotency_key, confirmed_at, failure_reason, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                paymentId,
                checkoutSession.checkoutSessionId(),
                "mock-gateway",
                "pay-" + paymentId,
                "CAPTURED",
                "USD",
                new BigDecimal("145.00"),
                "payment-" + paymentId,
                now,
                null,
                0L,
                now,
                now
        );
        jdbcTemplate.update(
                """
                insert into orders (
                    id, user_id, seller_id, payment_id, checkout_session_id, order_number, status, currency_code,
                    subtotal_amount, discount_amount, shipping_amount, total_amount, placed_at, version, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                orderId,
                checkoutSession.userId(),
                null,
                paymentId,
                checkoutSession.checkoutSessionId(),
                "ORD-" + orderId.toString().substring(0, 8),
                "PAID",
                "USD",
                new BigDecimal("145.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("145.00"),
                now,
                0L,
                now,
                now
        );
        return orderId;
    }

    private int readReservedQuantity(UUID variantId) {
        return jdbcTemplate.queryForObject(
                "select reserved_quantity from inventory_items where variant_id = ?",
                Integer.class,
                variantId
        );
    }

    private int readAvailableQuantity(UUID variantId) {
        return jdbcTemplate.queryForObject(
                "select available_quantity from inventory_items where variant_id = ?",
                Integer.class,
                variantId
        );
    }

    private int countReservationsByStatus(String status) {
        return jdbcTemplate.queryForObject(
                "select count(*) from inventory_reservations where status = ?",
                Integer.class,
                status
        );
    }

    private record CheckoutSessionFixture(UUID userId, UUID cartId, UUID checkoutSessionId) {
    }
}
