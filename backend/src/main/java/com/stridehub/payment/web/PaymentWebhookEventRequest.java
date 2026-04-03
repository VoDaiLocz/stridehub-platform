package com.stridehub.payment.web;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PaymentWebhookEventRequest(
        @NotBlank String eventId,
        @NotBlank String providerReference,
        @NotBlank String eventType,
        BigDecimal amount,
        String currency
) {
}
