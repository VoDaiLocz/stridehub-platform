package com.stridehub.cart.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CartItemUpsertRequest(
        @NotNull UUID variantId,
        @Positive int quantity
) {
}
