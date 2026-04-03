package com.stridehub.catalog.web;

import java.util.UUID;

public record CatalogBrandResponse(
        UUID id,
        String name,
        String slug
) {
}
