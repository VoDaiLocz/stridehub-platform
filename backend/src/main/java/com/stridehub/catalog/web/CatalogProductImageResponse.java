package com.stridehub.catalog.web;

import java.util.UUID;

public record CatalogProductImageResponse(
        UUID id,
        UUID variantId,
        String imageUrl,
        String altText,
        int sortOrder
) {
}
