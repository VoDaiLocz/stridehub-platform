package com.stridehub.order.web;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productVariantId,
        String sku,
        int quantity,
        BigDecimal unitPrice
) {
}
