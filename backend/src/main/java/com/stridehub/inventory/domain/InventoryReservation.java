package com.stridehub.inventory.domain;

import com.stridehub.catalog.domain.ProductVariant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "inventory_reservations")
public class InventoryReservation {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "checkout_session_id")
    private UUID checkoutSessionId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private InventoryReservationStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryReservation() {
    }

    public InventoryReservation(UUID id, ProductVariant variant, UUID checkoutSessionId, int quantity, Instant expiresAt) {
        this.id = id;
        this.variant = variant;
        this.checkoutSessionId = checkoutSessionId;
        this.quantity = quantity;
        this.status = InventoryReservationStatus.ACTIVE;
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public UUID getCheckoutSessionId() {
        return checkoutSessionId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public InventoryReservationStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive(Instant now) {
        return status == InventoryReservationStatus.ACTIVE && expiresAt.isAfter(now);
    }

    public void commit(UUID orderId) {
        this.orderId = orderId;
        this.status = InventoryReservationStatus.COMMITTED;
    }

    public void release() {
        this.status = InventoryReservationStatus.RELEASED;
    }

    public void expire() {
        this.status = InventoryReservationStatus.EXPIRED;
    }
}
