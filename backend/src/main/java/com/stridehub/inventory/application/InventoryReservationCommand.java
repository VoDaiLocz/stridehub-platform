package com.stridehub.inventory.application;

import java.util.UUID;

public record InventoryReservationCommand(
        UUID variantId,
        int quantity
) {
}
