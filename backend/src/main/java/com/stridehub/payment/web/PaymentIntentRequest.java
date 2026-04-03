package com.stridehub.payment.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentIntentRequest(
        @NotNull UUID checkoutSessionId
) {
}
