package com.stridehub.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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

    public Inventory getInventory() {
        return inventory;
    }

    public Payment getPayment() {
        return payment;
    }

    public Audit getAudit() {
        return audit;
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
}
