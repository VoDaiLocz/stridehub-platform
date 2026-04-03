package com.stridehub.catalog.web;

import com.stridehub.catalog.application.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public List<CatalogCategoryResponse> categories() {
        return catalogService.categories();
    }

    @GetMapping("/brands")
    public List<CatalogBrandResponse> brands() {
        return catalogService.brands();
    }

    @GetMapping("/products")
    public CatalogProductListResponse products(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color
    ) {
        return catalogService.products(category, brand, size, color);
    }

    @GetMapping("/products/{slug}")
    public CatalogProductDetailResponse productBySlug(@PathVariable String slug) {
        return catalogService.productBySlug(slug);
    }
}
