package com.stridehub.payment.infrastructure.provider;

import static org.assertj.core.api.Assertions.assertThat;

import com.stridehub.config.StridehubProperties;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class MockPaymentProviderTest {

    @Test
    void shouldCreateDeterministicRedirectPayloadAndValidateSignature() throws Exception {
        StridehubProperties properties = new StridehubProperties();
        properties.getPayment().setWebhookSecret("mock-webhook-secret");
        MockPaymentProvider provider = new MockPaymentProvider(properties);

        PaymentInitiation initiation = provider.initiate("checkout-123", new BigDecimal("145.00"), "USD");
        String payload = "{\"eventId\":\"evt-1\",\"providerReference\":\"" + initiation.providerReference()
                + "\",\"eventType\":\"payment.captured\",\"amount\":145.00,\"currency\":\"USD\"}";

        assertThat(initiation.providerReference()).startsWith("mock-pay-");
        assertThat(initiation.redirectUrl()).contains(initiation.providerReference());

        String signature = signPayload(payload, "mock-webhook-secret");
        assertThat(provider.verifyWebhookSignature(payload, signature)).isTrue();
        assertThat(provider.verifyWebhookSignature(payload, "bad-signature")).isFalse();
    }

    private String signPayload(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
