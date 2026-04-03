package com.stridehub.checkout.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CheckoutSessionResponse(
        UUID id,
        String status,
        Instant reservationExpiresAt,
        BigDecimal amount,
        String currency
) {
}
