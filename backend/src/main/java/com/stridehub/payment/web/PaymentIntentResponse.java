package com.stridehub.payment.web;

import java.util.UUID;

public record PaymentIntentResponse(
        UUID paymentId,
        String providerReference,
        String redirectUrl
) {
}
