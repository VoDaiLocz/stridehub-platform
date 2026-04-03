package com.stridehub.catalog.infrastructure;

import com.stridehub.catalog.domain.Product;
import com.stridehub.catalog.domain.ProductStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
            select distinct product
            from Product product
            join fetch product.brand brand
            join fetch product.category category
            where product.status = com.stridehub.catalog.domain.ProductStatus.ACTIVE
              and category.isActive = true
              and brand.isActive = true
              and (:categorySlug is null or category.slug = :categorySlug)
              and (:brandSlug is null or brand.slug = :brandSlug)
              and exists (
                  select 1
                  from ProductVariant variant
                  where variant.product = product
                    and variant.status = com.stridehub.catalog.domain.ProductVariantStatus.ACTIVE
                    and (:sizeValue is null or variant.sizeValue = :sizeValue)
                    and (:colorValue is null or variant.colorValue = :colorValue)
              )
            order by product.publishedAt desc, product.createdAt desc
            """)
    List<Product> findActiveProducts(
            @Param("categorySlug") String categorySlug,
            @Param("brandSlug") String brandSlug,
            @Param("sizeValue") String sizeValue,
            @Param("colorValue") String colorValue
    );

    @EntityGraph(attributePaths = {"brand", "category", "variants", "images"})
    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);
}
