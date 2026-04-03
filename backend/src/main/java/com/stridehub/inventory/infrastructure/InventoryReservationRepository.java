package com.stridehub.inventory.infrastructure;

import com.stridehub.inventory.domain.InventoryReservation;
import com.stridehub.inventory.domain.InventoryReservationStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    List<InventoryReservation> findByCheckoutSessionIdAndStatus(UUID checkoutSessionId, InventoryReservationStatus status);
}
