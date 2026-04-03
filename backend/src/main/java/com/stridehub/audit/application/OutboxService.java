package com.stridehub.audit.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stridehub.audit.domain.OutboxEvent;
import com.stridehub.audit.infrastructure.OutboxEventRepository;
import com.stridehub.common.id.CorrelationIdAccessor;
import com.stridehub.common.time.TimeProvider;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final CorrelationIdAccessor correlationIdAccessor;
    private final TimeProvider timeProvider;
    private final ObjectMapper objectMapper;

    public OutboxService(
            OutboxEventRepository outboxEventRepository,
            CorrelationIdAccessor correlationIdAccessor,
            TimeProvider timeProvider,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.correlationIdAccessor = correlationIdAccessor;
        this.timeProvider = timeProvider;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enqueue(String aggregateType, UUID aggregateId, String eventType, Object payload) {
        outboxEventRepository.save(new OutboxEvent(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                serialize(payload),
                timeProvider.now(),
                correlationIdAccessor.currentOrDefault()
        ));
    }

    private String serialize(Object value) {
        Object safeValue = value == null ? Map.of() : value;
        try {
            return objectMapper.writeValueAsString(safeValue);
        } catch (Exception exception) {
            return "{\"serializationError\":true}";
        }
    }
}
