package com.stridehub.cart.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID id,
        String status,
        String currencyCode,
        int totalItems,
        BigDecimal subtotalAmount,
        List<CartItemResponse> items
) {
}
