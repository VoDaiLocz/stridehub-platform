package com.stridehub.audit.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stridehub.audit.domain.AuditActorType;
import com.stridehub.audit.domain.AuditLog;
import com.stridehub.audit.infrastructure.AuditLogRepository;
import com.stridehub.common.id.CorrelationIdAccessor;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final CorrelationIdAccessor correlationIdAccessor;
    private final ObjectMapper objectMapper;

    public AuditService(
            AuditLogRepository auditLogRepository,
            CorrelationIdAccessor correlationIdAccessor,
            ObjectMapper objectMapper
    ) {
        this.auditLogRepository = auditLogRepository;
        this.correlationIdAccessor = correlationIdAccessor;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void recordUserAction(
            UUID actorUserId,
            String action,
            String entityType,
            UUID entityId,
            String reason,
            Object metadata
    ) {
        record(actorUserId, AuditActorType.USER, action, entityType, entityId, reason, metadata);
    }

    @Transactional
    public void recordSystemAction(
            String action,
            String entityType,
            UUID entityId,
            String reason,
            Object metadata
    ) {
        record(null, AuditActorType.SYSTEM, action, entityType, entityId, reason, metadata);
    }

    private void record(
            UUID actorUserId,
            AuditActorType actorType,
            String action,
            String entityType,
            UUID entityId,
            String reason,
            Object metadata
    ) {
        auditLogRepository.save(new AuditLog(
                UUID.randomUUID(),
                actorUserId,
                actorType,
                action,
                entityType,
                entityId,
                correlationIdAccessor.currentOrDefault(),
                reason,
                serialize(metadata)
        ));
    }

    private String serialize(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            return serialize(Map.of("serializationError", exception.getMessage()));
        }
    }
}
