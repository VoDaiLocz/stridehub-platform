package com.stridehub.catalog.domain;

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
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "size_value", nullable = false, length = 32)
    private String sizeValue;

    @Column(name = "color_value", nullable = false, length = 32)
    private String colorValue;

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "compare_at_amount", precision = 12, scale = 2)
    private BigDecimal compareAtAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProductVariantStatus status;

    @Version
    @Column(nullable = false)
    private long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductVariant() {
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getSku() {
        return sku;
    }

    public String getSizeValue() {
        return sizeValue;
    }

    public String getColorValue() {
        return colorValue;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public BigDecimal getCompareAtAmount() {
        return compareAtAmount;
    }

    public ProductVariantStatus getStatus() {
        return status;
    }
}
