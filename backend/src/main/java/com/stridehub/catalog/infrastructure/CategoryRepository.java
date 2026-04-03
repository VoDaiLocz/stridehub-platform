package com.stridehub.catalog.infrastructure;

import com.stridehub.catalog.domain.Category;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByIsActiveTrueOrderByNameAsc();
}
