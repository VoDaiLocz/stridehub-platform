package com.stridehub.catalog.web;

import java.util.UUID;

public record BrandSummaryResponse(
        UUID id,
        String name,
        String slug
) {
}
