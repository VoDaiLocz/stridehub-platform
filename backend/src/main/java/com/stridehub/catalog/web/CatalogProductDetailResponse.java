package com.stridehub.catalog.web;

import java.util.List;
import java.util.UUID;

public record CatalogProductDetailResponse(
        UUID id,
        String slug,
        String name,
        String shortDescription,
        String longDescription,
        String currencyCode,
        CategorySummaryResponse category,
        BrandSummaryResponse brand,
        List<ProductVariantResponse> variants,
        List<CatalogProductImageResponse> images
) {
}
