package com.stridehub.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "stridehub")
public class StridehubProperties {

    @Valid
    private final Inventory inventory = new Inventory();

    @Valid
    private final Payment payment = new Payment();

    @Valid
    private final Audit audit = new Audit();

    @Valid
    private final Web web = new Web();

    public Inventory getInventory() {
        return inventory;
    }

    public Payment getPayment() {
        return payment;
    }

    public Audit getAudit() {
        return audit;
    }

    public Web getWeb() {
        return web;
    }

    public static class Inventory {

        @NotNull
        private Duration reservationTtl = Duration.ofMinutes(10);

        public Duration getReservationTtl() {
            return reservationTtl;
        }

        public void setReservationTtl(Duration reservationTtl) {
            this.reservationTtl = reservationTtl;
        }
    }

    public static class Payment {

        @NotBlank
        private String provider = "mock-gateway";

        @NotBlank
        private String webhookSignatureHeader = "X-Signature";

        @NotBlank
        private String webhookSecret = "mock-webhook-secret";

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getWebhookSignatureHeader() {
            return webhookSignatureHeader;
        }

        public void setWebhookSignatureHeader(String webhookSignatureHeader) {
            this.webhookSignatureHeader = webhookSignatureHeader;
        }

        public String getWebhookSecret() {
            return webhookSecret;
        }

        public void setWebhookSecret(String webhookSecret) {
            this.webhookSecret = webhookSecret;
        }
    }

    public static class Audit {

        @NotBlank
        private String correlationIdHeader = "X-Correlation-Id";

        public String getCorrelationIdHeader() {
            return correlationIdHeader;
        }

        public void setCorrelationIdHeader(String correlationIdHeader) {
            this.correlationIdHeader = correlationIdHeader;
        }
    }

    public static class Web {

        @NotNull
        private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:5173"));

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
