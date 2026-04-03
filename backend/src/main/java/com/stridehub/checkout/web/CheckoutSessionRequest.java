package com.stridehub.checkout.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutSessionRequest(
        @NotNull UUID shippingAddressId
) {
}
