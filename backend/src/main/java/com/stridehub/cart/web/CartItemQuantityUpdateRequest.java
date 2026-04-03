package com.stridehub.cart.web;

import jakarta.validation.constraints.Positive;

public record CartItemQuantityUpdateRequest(
        @Positive int quantity
) {
}
