package com.stridehub.payment.infrastructure.provider;

import com.stridehub.config.StridehubProperties;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentProvider implements PaymentProvider {

    private final StridehubProperties stridehubProperties;

    public MockPaymentProvider(StridehubProperties stridehubProperties) {
        this.stridehubProperties = stridehubProperties;
    }

    @Override
    public PaymentInitiation initiate(String idempotencyKey, BigDecimal amount, String currencyCode) {
        String providerReference = "mock-pay-" + UUID.randomUUID();
        String redirectUrl = "https://mock-gateway.stridehub.local/pay/" + providerReference
                + "?amount=" + amount
                + "&currency=" + currencyCode
                + "&request=" + idempotencyKey;
        return new PaymentInitiation(providerReference, redirectUrl);
    }

    @Override
    public boolean verifyWebhookSignature(String rawPayload, String signature) {
        if (signature == null || signature.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    stridehubProperties.getPayment().getWebhookSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));
            byte[] digest = mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(digest);
            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to verify mock payment signature", exception);
        }
    }
}
