package com.stridehub.catalog.web;

import java.util.UUID;

public record CatalogProductListItemResponse(
        UUID id,
        String slug,
        String name,
        String shortDescription,
        String currencyCode,
        CategorySummaryResponse category,
        BrandSummaryResponse brand,
        ProductVariantResponse primaryVariant,
        String primaryImageUrl
) {
}
