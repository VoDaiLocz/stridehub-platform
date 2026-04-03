package com.stridehub.order.domain;

import com.stridehub.checkout.domain.CheckoutSession;
import com.stridehub.identity.domain.User;
import com.stridehub.payment.domain.Payment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "seller_id")
    private UUID sellerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checkout_session_id", nullable = false, unique = true)
    private CheckoutSession checkoutSession;

    @Column(name = "order_number", nullable = false, unique = true, length = 64)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

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

    @Column(name = "placed_at")
    private Instant placedAt;

    @Version
    @Column(nullable = false)
    private long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> items = new LinkedHashSet<>();

    protected Order() {
    }

    public Order(
            UUID id,
            User user,
            UUID sellerId,
            Payment payment,
            CheckoutSession checkoutSession,
            String orderNumber,
            String currencyCode,
            BigDecimal subtotalAmount,
            BigDecimal discountAmount,
            BigDecimal shippingAmount,
            BigDecimal totalAmount,
            Instant placedAt
    ) {
        this.id = id;
        this.user = user;
        this.sellerId = sellerId;
        this.payment = payment;
        this.checkoutSession = checkoutSession;
        this.orderNumber = orderNumber;
        this.currencyCode = currencyCode;
        this.subtotalAmount = subtotalAmount;
        this.discountAmount = discountAmount;
        this.shippingAmount = shippingAmount;
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
        this.status = OrderStatus.PAID;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> orderedItems() {
        return items.stream()
                .sorted(Comparator.comparing(OrderItem::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(OrderItem::getId))
                .toList();
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }
}
