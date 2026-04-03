package com.stridehub.catalog.web;

import java.util.List;

public record CatalogProductListResponse(
        List<CatalogProductListItemResponse> items
) {
}
