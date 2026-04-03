package com.stridehub.order.web;

import com.stridehub.order.application.OrderService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderSummaryResponse> listOrders(JwtAuthenticationToken authentication) {
        return orderService.listOrders(currentUserId(authentication));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            JwtAuthenticationToken authentication,
            @PathVariable UUID orderId
    ) {
        return orderService.getOrder(currentUserId(authentication), orderId);
    }

    private UUID currentUserId(JwtAuthenticationToken authentication) {
        return UUID.fromString(authentication.getToken().getSubject());
    }
}
