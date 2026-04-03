package com.stridehub.checkout.domain;

public enum CheckoutSessionStatus {
    PENDING_PAYMENT,
    PAYMENT_INITIATED,
    PAYMENT_FAILED,
    COMPLETED,
    CANCELLED,
    EXPIRED
}
