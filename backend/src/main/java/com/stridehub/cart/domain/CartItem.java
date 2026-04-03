package com.stridehub.cart.domain;

import com.stridehub.catalog.domain.ProductVariant;
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
@Table(name = "cart_items")
public class CartItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price_amount", precision = 12, scale = 2)
    private BigDecimal unitPriceAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CartItem() {
    }

    public CartItem(UUID id, Cart cart, ProductVariant variant, int quantity, BigDecimal unitPriceAmount) {
        this.id = id;
        this.cart = cart;
        this.variant = variant;
        this.quantity = quantity;
        this.unitPriceAmount = unitPriceAmount;
    }

    public UUID getId() {
        return id;
    }

    public ProductVariant getVariant() {
        return variant;
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

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void refreshUnitPrice(BigDecimal priceAmount) {
        this.unitPriceAmount = priceAmount;
    }
}
