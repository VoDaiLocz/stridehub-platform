package com.stridehub.cart.infrastructure;

import com.stridehub.catalog.domain.ProductVariant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    @Query("""
            select variant from ProductVariant variant
            join fetch variant.product product
            where variant.id = :variantId
            """)
    Optional<ProductVariant> findDetailedById(UUID variantId);
}
