package com.stridehub.catalog.infrastructure;

import com.stridehub.catalog.domain.Brand;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    List<Brand> findByIsActiveTrueOrderByNameAsc();
}
