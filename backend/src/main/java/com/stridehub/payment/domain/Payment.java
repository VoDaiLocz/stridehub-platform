package com.stridehub.payment.domain;

import com.stridehub.checkout.domain.CheckoutSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checkout_session_id", nullable = false, unique = true)
    private CheckoutSession checkoutSession;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_payment_ref", nullable = false, unique = true, length = 128)
    private String providerPaymentRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentStatus status;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Version
    @Column(nullable = false)
    private long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Payment() {
    }

    public Payment(
            UUID id,
            CheckoutSession checkoutSession,
            String provider,
            String providerPaymentRef,
            PaymentStatus status,
            String currencyCode,
            BigDecimal amount,
            String idempotencyKey
    ) {
        this.id = id;
        this.checkoutSession = checkoutSession;
        this.provider = provider;
        this.providerPaymentRef = providerPaymentRef;
        this.status = status;
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getId() {
        return id;
    }

    public CheckoutSession getCheckoutSession() {
        return checkoutSession;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderPaymentRef() {
        return providerPaymentRef;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public boolean isCaptured() {
        return status == PaymentStatus.CAPTURED;
    }

    public void markRequiresAction() {
        this.status = PaymentStatus.REQUIRES_ACTION;
        this.failureReason = null;
    }

    public void markCaptured(Instant confirmedAt) {
        this.status = PaymentStatus.CAPTURED;
        this.confirmedAt = confirmedAt;
        this.failureReason = null;
    }

    public void markFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }
}
