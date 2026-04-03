package com.stridehub.inventory.infrastructure;

import com.stridehub.inventory.domain.InventoryItem;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findByVariant_Id(UUID variantId);
}
