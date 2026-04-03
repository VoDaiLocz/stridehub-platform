package com.stridehub.checkout.domain;

import com.stridehub.cart.domain.Cart;
import com.stridehub.identity.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "checkout_sessions")
public class CheckoutSession {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false, unique = true)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CheckoutSessionStatus status;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "shipping_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CheckoutSession() {
    }

    public CheckoutSession(
            UUID id,
            User user,
            Cart cart,
            String currencyCode,
            BigDecimal subtotalAmount,
            BigDecimal discountAmount,
            BigDecimal shippingAmount,
            BigDecimal totalAmount,
            Instant expiresAt,
            String idempotencyKey
    ) {
        this.id = id;
        this.user = user;
        this.cart = cart;
        this.currencyCode = currencyCode;
        this.subtotalAmount = subtotalAmount;
        this.discountAmount = discountAmount;
        this.shippingAmount = shippingAmount;
        this.totalAmount = totalAmount;
        this.expiresAt = expiresAt;
        this.idempotencyKey = idempotencyKey;
        this.status = CheckoutSessionStatus.PENDING_PAYMENT;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Cart getCart() {
        return cart;
    }

    public CheckoutSessionStatus getStatus() {
        return status;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void refresh(
            String currencyCode,
            BigDecimal subtotalAmount,
            BigDecimal discountAmount,
            BigDecimal shippingAmount,
            BigDecimal totalAmount,
            Instant expiresAt,
            String idempotencyKey
    ) {
        this.currencyCode = currencyCode;
        this.subtotalAmount = subtotalAmount;
        this.discountAmount = discountAmount;
        this.shippingAmount = shippingAmount;
        this.totalAmount = totalAmount;
        this.expiresAt = expiresAt;
        this.idempotencyKey = idempotencyKey;
        this.status = CheckoutSessionStatus.PENDING_PAYMENT;
    }

    public void markPaymentInitiated() {
        this.status = CheckoutSessionStatus.PAYMENT_INITIATED;
    }

    public void markPaymentFailed() {
        this.status = CheckoutSessionStatus.PAYMENT_FAILED;
    }

    public void markCompleted() {
        this.status = CheckoutSessionStatus.COMPLETED;
    }

    public void markCancelled() {
        this.status = CheckoutSessionStatus.CANCELLED;
    }

    public void markExpired() {
        this.status = CheckoutSessionStatus.EXPIRED;
    }
}
