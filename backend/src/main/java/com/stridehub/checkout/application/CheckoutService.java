package com.stridehub.checkout.application;

import com.stridehub.cart.domain.Cart;
import com.stridehub.cart.domain.CartItem;
import com.stridehub.cart.domain.CartStatus;
import com.stridehub.cart.infrastructure.CartRepository;
import com.stridehub.catalog.domain.Product;
import com.stridehub.catalog.domain.ProductStatus;
import com.stridehub.catalog.domain.ProductVariant;
import com.stridehub.catalog.domain.ProductVariantStatus;
import com.stridehub.checkout.domain.CheckoutSession;
import com.stridehub.checkout.domain.CheckoutSessionStatus;
import com.stridehub.checkout.infrastructure.CheckoutSessionRepository;
import com.stridehub.checkout.web.CheckoutSessionResponse;
import com.stridehub.common.exception.ConflictException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.config.StridehubProperties;
import com.stridehub.inventory.application.InventoryReservationCommand;
import com.stridehub.inventory.application.InventoryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final InventoryService inventoryService;
    private final TimeProvider timeProvider;
    private final StridehubProperties stridehubProperties;

    public CheckoutService(
            CartRepository cartRepository,
            CheckoutSessionRepository checkoutSessionRepository,
            InventoryService inventoryService,
            TimeProvider timeProvider,
            StridehubProperties stridehubProperties
    ) {
        this.cartRepository = cartRepository;
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.inventoryService = inventoryService;
        this.timeProvider = timeProvider;
        this.stridehubProperties = stridehubProperties;
    }

    @Transactional
    public CheckoutSessionResponse createSession(UUID userId) {
        Cart cart = cartRepository.findDetailedByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new ConflictException("checkout.empty_cart", "Cannot create checkout for an empty cart"));

        List<CartItem> items = cart.orderedItems();
        if (items.isEmpty()) {
            throw new ConflictException("checkout.empty_cart", "Cannot create checkout for an empty cart");
        }

        String currencyCode = determineCurrency(items);
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        List<InventoryReservationCommand> commands = new ArrayList<>();

        for (CartItem item : items) {
            ProductVariant variant = item.getVariant();
            Product product = variant.getProduct();
            if (variant.getStatus() != ProductVariantStatus.ACTIVE || product.getStatus() != ProductStatus.ACTIVE) {
                throw new ConflictException("checkout.variant_unavailable", "One or more cart variants are unavailable");
            }

            item.refreshUnitPrice(variant.getPriceAmount());
            BigDecimal currentLineTotal = variant.getPriceAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotalAmount = subtotalAmount.add(currentLineTotal);
            commands.add(new InventoryReservationCommand(variant.getId(), item.getQuantity()));
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal shippingAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotalAmount.add(shippingAmount).subtract(discountAmount);
        Instant expiresAt = timeProvider.now().plus(stridehubProperties.getInventory().getReservationTtl());

        CheckoutSession checkoutSession = checkoutSessionRepository.findByCart_Id(cart.getId())
                .orElse(null);
        if (checkoutSession != null) {
            checkoutSession = refreshExistingSession(
                    checkoutSession,
                    currencyCode,
                    subtotalAmount,
                    discountAmount,
                    shippingAmount,
                    totalAmount,
                    expiresAt
            );
        } else {
            checkoutSession = new CheckoutSession(
                    UUID.randomUUID(),
                    cart.getUser(),
                    cart,
                    currencyCode,
                    subtotalAmount,
                    discountAmount,
                    shippingAmount,
                    totalAmount,
                    expiresAt,
                    newIdempotencyKey()
            );
        }

        CheckoutSession savedSession = checkoutSessionRepository.save(checkoutSession);
        inventoryService.reserveForCheckout(savedSession.getId(), commands, expiresAt);

        return new CheckoutSessionResponse(
                savedSession.getId(),
                savedSession.getStatus().name(),
                savedSession.getExpiresAt(),
                savedSession.getTotalAmount(),
                savedSession.getCurrencyCode()
        );
    }

    @Transactional
    public void expireExpiredSessions() {
        Instant now = timeProvider.now();
        List<CheckoutSession> expiredSessions = checkoutSessionRepository.findByStatusInAndExpiresAtBefore(
                List.copyOf(EnumSet.of(CheckoutSessionStatus.PENDING_PAYMENT, CheckoutSessionStatus.PAYMENT_INITIATED)),
                now
        );

        for (CheckoutSession checkoutSession : expiredSessions) {
            inventoryService.expireCheckoutReservations(checkoutSession.getId());
            checkoutSession.markExpired();
        }
    }

    private CheckoutSession refreshExistingSession(
            CheckoutSession existing,
            String currencyCode,
            BigDecimal subtotalAmount,
            BigDecimal discountAmount,
            BigDecimal shippingAmount,
            BigDecimal totalAmount,
            Instant expiresAt
    ) {
        inventoryService.releaseCheckoutReservations(existing.getId());
        existing.refresh(
                currencyCode,
                subtotalAmount,
                discountAmount,
                shippingAmount,
                totalAmount,
                expiresAt,
                newIdempotencyKey()
        );
        return existing;
    }

    private String determineCurrency(List<CartItem> items) {
        String currency = items.getFirst().getVariant().getProduct().getCurrencyCode();
        boolean mixedCurrencies = items.stream()
                .map(item -> item.getVariant().getProduct().getCurrencyCode())
                .anyMatch(value -> !currency.equals(value));

        if (mixedCurrencies) {
            throw new ConflictException("checkout.currency_mismatch", "Cart items must share the same currency");
        }
        return currency;
    }

    private String newIdempotencyKey() {
        return "checkout-" + UUID.randomUUID();
    }
}
