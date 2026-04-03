package com.stridehub.catalog.web;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        String sku,
        String size,
        String color,
        BigDecimal price,
        BigDecimal compareAtPrice
) {
}
