package com.stridehub.payment.infrastructure.provider;

import java.math.BigDecimal;

public interface PaymentProvider {

    PaymentInitiation initiate(
            String idempotencyKey,
            BigDecimal amount,
            String currencyCode
    );

    boolean verifyWebhookSignature(String rawPayload, String signature);
}
