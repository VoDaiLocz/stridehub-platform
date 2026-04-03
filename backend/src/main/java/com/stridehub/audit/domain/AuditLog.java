package com.stridehub.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    private UUID id;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 32)
    private AuditActorType actorType;

    @Column(nullable = false, length = 128)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 128)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "correlation_id", nullable = false, length = 64)
    private String correlationId;

    @Column(length = 255)
    private String reason;

    @Lob
    @Column
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AuditLog() {
    }

    public AuditLog(
            UUID id,
            UUID actorUserId,
            AuditActorType actorType,
            String action,
            String entityType,
            UUID entityId,
            String correlationId,
            String reason,
            String metadata
    ) {
        this.id = id;
        this.actorUserId = actorUserId;
        this.actorType = actorType;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.correlationId = correlationId;
        this.reason = reason;
        this.metadata = metadata;
    }
}
