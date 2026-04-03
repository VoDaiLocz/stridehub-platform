package com.stridehub.inventory.application;

import com.stridehub.catalog.domain.ProductVariant;
import com.stridehub.common.exception.ConflictException;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.inventory.domain.InventoryItem;
import com.stridehub.inventory.domain.InventoryReservation;
import com.stridehub.inventory.domain.InventoryReservationStatus;
import com.stridehub.inventory.infrastructure.InventoryItemRepository;
import com.stridehub.inventory.infrastructure.InventoryReservationRepository;
import com.stridehub.inventory.infrastructure.ProductVariantRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final ProductVariantRepository productVariantRepository;
    private final TimeProvider timeProvider;

    public InventoryService(
            InventoryItemRepository inventoryItemRepository,
            InventoryReservationRepository inventoryReservationRepository,
            ProductVariantRepository productVariantRepository,
            TimeProvider timeProvider
    ) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.productVariantRepository = productVariantRepository;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public List<InventoryReservation> reserveForCheckout(
            UUID checkoutSessionId,
            List<InventoryReservationCommand> commands,
            Instant expiresAt
    ) {
        return commands.stream()
                .map(command -> reserveSingle(checkoutSessionId, command, expiresAt))
                .toList();
    }

    @Transactional
    public void releaseCheckoutReservations(UUID checkoutSessionId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findByCheckoutSessionIdAndStatus(
                checkoutSessionId,
                InventoryReservationStatus.ACTIVE
        );
        for (InventoryReservation reservation : reservations) {
            InventoryItem inventoryItem = lockedInventoryItem(reservation.getVariant().getId());
            inventoryItem.release(reservation.getQuantity());
            reservation.release();
        }
    }

    @Transactional
    public void commitCheckoutReservations(UUID checkoutSessionId, UUID orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findByCheckoutSessionIdAndStatus(
                checkoutSessionId,
                InventoryReservationStatus.ACTIVE
        );
        for (InventoryReservation reservation : reservations) {
            InventoryItem inventoryItem = lockedInventoryItem(reservation.getVariant().getId());
            inventoryItem.commit(reservation.getQuantity());
            reservation.commit(orderId);
        }
    }

    @Transactional
    public void expireCheckoutReservations(UUID checkoutSessionId) {
        Instant now = timeProvider.now();
        List<InventoryReservation> reservations = inventoryReservationRepository.findByCheckoutSessionIdAndStatus(
                checkoutSessionId,
                InventoryReservationStatus.ACTIVE
        );
        for (InventoryReservation reservation : reservations) {
            if (reservation.isActive(now)) {
                continue;
            }
            InventoryItem inventoryItem = lockedInventoryItem(reservation.getVariant().getId());
            inventoryItem.release(reservation.getQuantity());
            reservation.expire();
        }
    }

    private InventoryReservation reserveSingle(
            UUID checkoutSessionId,
            InventoryReservationCommand command,
            Instant expiresAt
    ) {
        ProductVariant variant = productVariantRepository.findById(command.variantId())
                .orElseThrow(() -> new NotFoundException("inventory.variant_not_found", "Variant was not found"));
        InventoryItem inventoryItem = lockedInventoryItem(command.variantId());
        if (command.quantity() <= 0) {
            throw new ConflictException("inventory.invalid_quantity", "Reservation quantity must be positive");
        }
        if (inventoryItem.reservableQuantity() < command.quantity()) {
            throw new ConflictException("inventory.insufficient_stock", "Insufficient stock for reservation");
        }
        inventoryItem.reserve(command.quantity());
        InventoryReservation reservation = new InventoryReservation(
                UUID.randomUUID(),
                variant,
                checkoutSessionId,
                command.quantity(),
                expiresAt
        );
        return inventoryReservationRepository.save(reservation);
    }

    private InventoryItem lockedInventoryItem(UUID variantId) {
        return inventoryItemRepository.findByVariant_Id(variantId)
                .orElseThrow(() -> new NotFoundException("inventory.item_not_found", "Inventory item was not found"));
    }
}
