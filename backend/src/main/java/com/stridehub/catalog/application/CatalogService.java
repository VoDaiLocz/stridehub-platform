package com.stridehub.catalog.application;

import com.stridehub.catalog.domain.Brand;
import com.stridehub.catalog.domain.Category;
import com.stridehub.catalog.domain.Product;
import com.stridehub.catalog.domain.ProductImage;
import com.stridehub.catalog.domain.ProductStatus;
import com.stridehub.catalog.domain.ProductVariant;
import com.stridehub.catalog.infrastructure.BrandRepository;
import com.stridehub.catalog.infrastructure.CategoryRepository;
import com.stridehub.catalog.infrastructure.ProductRepository;
import com.stridehub.catalog.web.BrandSummaryResponse;
import com.stridehub.catalog.web.CatalogBrandResponse;
import com.stridehub.catalog.web.CatalogCategoryResponse;
import com.stridehub.catalog.web.CatalogProductDetailResponse;
import com.stridehub.catalog.web.CatalogProductImageResponse;
import com.stridehub.catalog.web.CatalogProductListItemResponse;
import com.stridehub.catalog.web.CatalogProductListResponse;
import com.stridehub.catalog.web.CategorySummaryResponse;
import com.stridehub.catalog.web.ProductVariantResponse;
import com.stridehub.common.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public CatalogService(
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> categories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogBrandResponse> brands() {
        return brandRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::toBrandResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogProductListResponse products(String category, String brand, String size, String color) {
        List<CatalogProductListItemResponse> items = productRepository.findActiveProducts(category, brand, size, color).stream()
                .map(this::toProductListItem)
                .toList();
        return new CatalogProductListResponse(items);
    }

    @Transactional(readOnly = true)
    public CatalogProductDetailResponse productBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatus(slug, ProductStatus.ACTIVE)
                .filter(found -> found.getBrand().isActive() && found.getCategory().isActive())
                .orElseThrow(() -> new NotFoundException("catalog.product_not_found", "Catalog product was not found"));
        return toProductDetail(product);
    }

    private CatalogCategoryResponse toCategoryResponse(Category category) {
        return new CatalogCategoryResponse(category.getId(), category.getName(), category.getSlug());
    }

    private CatalogBrandResponse toBrandResponse(Brand brand) {
        return new CatalogBrandResponse(brand.getId(), brand.getName(), brand.getSlug());
    }

    private CatalogProductListItemResponse toProductListItem(Product product) {
        ProductVariant primaryVariant = product.activeVariants().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("catalog.variant_not_found", "Active variant was not found"));
        String primaryImageUrl = product.orderedImages().stream()
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
        return new CatalogProductListItemResponse(
                product.getId(),
                product.getSlug(),
                product.getName(),
                product.getShortDescription(),
                product.getCurrencyCode(),
                new CategorySummaryResponse(product.getCategory().getId(), product.getCategory().getName(), product.getCategory().getSlug()),
                new BrandSummaryResponse(product.getBrand().getId(), product.getBrand().getName(), product.getBrand().getSlug()),
                new ProductVariantResponse(
                        primaryVariant.getId(),
                        primaryVariant.getSku(),
                        primaryVariant.getSizeValue(),
                        primaryVariant.getColorValue(),
                        primaryVariant.getPriceAmount(),
                        primaryVariant.getCompareAtAmount()
                ),
                primaryImageUrl
        );
    }

    private CatalogProductDetailResponse toProductDetail(Product product) {
        List<ProductVariantResponse> variants = product.activeVariants().stream()
                .map(variant -> new ProductVariantResponse(
                        variant.getId(),
                        variant.getSku(),
                        variant.getSizeValue(),
                        variant.getColorValue(),
                        variant.getPriceAmount(),
                        variant.getCompareAtAmount()
                ))
                .toList();
        List<CatalogProductImageResponse> images = product.orderedImages().stream()
                .map(image -> new CatalogProductImageResponse(
                        image.getId(),
                        image.getVariant() != null ? image.getVariant().getId() : null,
                        image.getImageUrl(),
                        image.getAltText(),
                        image.getSortOrder()
                ))
                .toList();
        return new CatalogProductDetailResponse(
                product.getId(),
                product.getSlug(),
                product.getName(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getCurrencyCode(),
                new CategorySummaryResponse(product.getCategory().getId(), product.getCategory().getName(), product.getCategory().getSlug()),
                new BrandSummaryResponse(product.getBrand().getId(), product.getBrand().getName(), product.getBrand().getSlug()),
                variants,
                images
        );
    }
}
