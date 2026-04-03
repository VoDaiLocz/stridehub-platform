package com.stridehub.order.domain;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    ALLOCATED,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUND_PENDING,
    REFUNDED,
    RETURN_REQUESTED,
    RETURNED
}
