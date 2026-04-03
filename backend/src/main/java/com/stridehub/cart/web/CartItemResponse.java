package com.stridehub.cart.web;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String productSlug,
        UUID variantId,
        String sku,
        String sizeValue,
        String colorValue,
        String currencyCode,
        int quantity,
        BigDecimal unitPriceAmount,
        BigDecimal lineTotalAmount
) {
}
