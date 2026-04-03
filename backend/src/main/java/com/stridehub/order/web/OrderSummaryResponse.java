package com.stridehub.order.web;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String currency
) {
}
