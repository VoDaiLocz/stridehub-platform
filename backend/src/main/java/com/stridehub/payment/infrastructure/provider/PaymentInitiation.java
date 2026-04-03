package com.stridehub.payment.infrastructure.provider;

public record PaymentInitiation(
        String providerReference,
        String redirectUrl
) {
}
