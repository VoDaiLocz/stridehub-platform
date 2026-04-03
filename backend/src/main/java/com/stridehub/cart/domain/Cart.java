package com.stridehub.cart.domain;

import com.stridehub.catalog.domain.ProductVariant;
import com.stridehub.identity.domain.User;
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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CartStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> items = new LinkedHashSet<>();

    protected Cart() {
    }

    public Cart(UUID id, User user) {
        this.id = id;
        this.user = user;
        this.status = CartStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartItem> orderedItems() {
        return items.stream()
                .sorted(Comparator.comparing(CartItem::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(CartItem::getId))
                .toList();
    }

    public void addItem(ProductVariant variant, int quantity) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.increaseQuantity(quantity);
            item.refreshUnitPrice(variant.getPriceAmount());
            return;
        }

        items.add(new CartItem(UUID.randomUUID(), this, variant, quantity, variant.getPriceAmount()));
    }

    public Optional<CartItem> findItem(UUID itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    public void removeItem(UUID itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }
}
