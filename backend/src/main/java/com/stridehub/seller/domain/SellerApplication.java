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
@Table(name = "seller_applications")
public class SellerApplication {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;

    @Column(name = "legal_name", length = 255)
    private String legalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SellerStatus status;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SellerApplication() {
    }

    public SellerApplication(UUID id, User user, String storeName, String legalName, Instant submittedAt) {
        this.id = id;
        this.user = user;
        this.storeName = normalize(storeName);
        this.legalName = normalize(legalName);
        this.status = SellerStatus.PENDING;
        this.submittedAt = submittedAt;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getLegalName() {
        return legalName;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
