package com.stridehub.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payment_attempts")
public class PaymentAttempt {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "provider_status", nullable = false, length = 64)
    private String providerStatus;

    @Column(name = "provider_request_id", length = 128)
    private String providerRequestId;

    @Column(name = "raw_reference", length = 255)
    private String rawReference;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @Column(nullable = false)
    private boolean success;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PaymentAttempt() {
    }

    public PaymentAttempt(
            UUID id,
            Payment payment,
            String providerStatus,
            String providerRequestId,
            String rawReference,
            Instant attemptedAt,
            boolean success
    ) {
        this.id = id;
        this.payment = payment;
        this.providerStatus = providerStatus;
        this.providerRequestId = providerRequestId;
        this.rawReference = rawReference;
        this.attemptedAt = attemptedAt;
        this.success = success;
    }
}
