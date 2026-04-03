package com.stridehub.order.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String currency,
        List<OrderItemResponse> items
) {
}
