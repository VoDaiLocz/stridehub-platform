package com.stridehub.seller.web;

import java.time.Instant;
import java.util.UUID;

public record SellerApplicationResponse(
        UUID id,
        String storeName,
        String legalName,
        String status,
        Instant submittedAt,
        Instant reviewedAt,
        String rejectionReason
) {
}
