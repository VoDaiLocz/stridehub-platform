package com.stridehub.catalog.web;

import java.util.UUID;

public record CatalogCategoryResponse(
        UUID id,
        String name,
        String slug
) {
}
