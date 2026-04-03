package com.stridehub.audit.domain;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
