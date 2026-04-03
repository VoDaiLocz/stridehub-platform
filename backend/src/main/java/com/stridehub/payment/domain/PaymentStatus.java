package com.stridehub.payment.domain;

public enum PaymentStatus {
    INITIATED,
    REQUIRES_ACTION,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    CANCELLED,
    REFUNDED
}
