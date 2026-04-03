package com.stridehub.inventory.infrastructure;

import com.stridehub.catalog.domain.ProductVariant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
}
