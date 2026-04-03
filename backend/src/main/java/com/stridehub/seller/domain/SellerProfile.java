package com.stridehub.seller.domain;

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
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "seller_profiles")
public class SellerProfile {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "legal_name", length = 255)
    private String legalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SellerStatus status;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "suspended_at")
    private Instant suspendedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SellerProfile() {
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLegalName() {
        return legalName;
    }

    public SellerStatus getStatus() {
        return status;
    }
}
