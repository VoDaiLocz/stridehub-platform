package com.stridehub.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "order_items")
public class OrderItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @Column(nullable = false, length = 64)
    private String sku;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "variant_name", nullable = false, length = 255)
    private String variantName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPriceAmount;

    @Column(name = "total_price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPriceAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrderItem() {
    }

    public OrderItem(
            UUID id,
            Order order,
            UUID productId,
            UUID variantId,
            String sku,
            String productName,
            String variantName,
            int quantity,
            BigDecimal unitPriceAmount,
            BigDecimal totalPriceAmount
    ) {
        this.id = id;
        this.order = order;
        this.productId = productId;
        this.variantId = variantId;
        this.sku = sku;
        this.productName = productName;
        this.variantName = variantName;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
        this.totalPriceAmount = totalPriceAmount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPriceAmount() {
        return unitPriceAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
