package com.stridehub.order.application;

import com.stridehub.audit.application.AuditService;
import com.stridehub.audit.application.OutboxService;
import com.stridehub.cart.domain.Cart;
import com.stridehub.cart.domain.CartItem;
import com.stridehub.checkout.domain.CheckoutSession;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.inventory.application.InventoryService;
import com.stridehub.order.domain.Order;
import com.stridehub.order.domain.OrderItem;
import com.stridehub.order.infrastructure.OrderRepository;
import com.stridehub.order.web.OrderItemResponse;
import com.stridehub.order.web.OrderResponse;
import com.stridehub.order.web.OrderSummaryResponse;
import com.stridehub.payment.domain.Payment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final TimeProvider timeProvider;
    private final AuditService auditService;
    private final OutboxService outboxService;

    public OrderService(
            OrderRepository orderRepository,
            InventoryService inventoryService,
            TimeProvider timeProvider,
            AuditService auditService,
            OutboxService outboxService
    ) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.timeProvider = timeProvider;
        this.auditService = auditService;
        this.outboxService = outboxService;
    }

    @Transactional
    public Order confirmCapturedPayment(Payment payment) {
        return orderRepository.findByPayment_Id(payment.getId())
                .orElseGet(() -> createOrder(payment));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new NotFoundException("order.not_found", "Order was not found"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listOrders(UUID userId) {
        return orderRepository.findAllByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getStatus().name(),
                        order.getTotalAmount(),
                        order.getCurrencyCode()
                ))
                .toList();
    }

    private Order createOrder(Payment payment) {
        CheckoutSession checkoutSession = payment.getCheckoutSession();
        Cart cart = checkoutSession.getCart();
        Order order = new Order(
                UUID.randomUUID(),
                checkoutSession.getUser(),
                detectSellerId(cart.orderedItems()),
                payment,
                checkoutSession,
                generateOrderNumber(),
                checkoutSession.getCurrencyCode(),
                checkoutSession.getSubtotalAmount(),
                checkoutSession.getDiscountAmount(),
                checkoutSession.getShippingAmount(),
                checkoutSession.getTotalAmount(),
                timeProvider.now()
        );

        for (CartItem cartItem : cart.orderedItems()) {
            BigDecimal unitPrice = cartItem.getUnitPriceAmount();
            order.addItem(new OrderItem(
                    UUID.randomUUID(),
                    order,
                    cartItem.getVariant().getProduct().getId(),
                    cartItem.getVariant().getId(),
                    cartItem.getVariant().getSku(),
                    cartItem.getVariant().getProduct().getName(),
                    cartItem.getVariant().getColorValue() + " / " + cartItem.getVariant().getSizeValue(),
                    cartItem.getQuantity(),
                    unitPrice,
                    unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            ));
        }

        Order savedOrder = orderRepository.save(order);
        inventoryService.commitCheckoutReservations(checkoutSession.getId(), savedOrder.getId());
        checkoutSession.markCompleted();
        cart.markCheckedOut();
        auditService.recordSystemAction(
                "order.created",
                "order",
                savedOrder.getId(),
                null,
                java.util.Map.of(
                        "orderNumber", savedOrder.getOrderNumber(),
                        "paymentId", payment.getId(),
                        "checkoutSessionId", checkoutSession.getId(),
                        "userId", checkoutSession.getUser().getId()
                )
        );
        outboxService.enqueue(
                "order",
                savedOrder.getId(),
                "order.created",
                java.util.Map.of(
                        "orderId", savedOrder.getId(),
                        "orderNumber", savedOrder.getOrderNumber(),
                        "paymentId", payment.getId(),
                        "checkoutSessionId", checkoutSession.getId(),
                        "userId", checkoutSession.getUser().getId(),
                        "totalAmount", savedOrder.getTotalAmount(),
                        "currencyCode", savedOrder.getCurrencyCode()
                )
        );
        return savedOrder;
    }

    private UUID detectSellerId(List<CartItem> items) {
        List<UUID> sellerIds = items.stream()
                .map(item -> item.getVariant().getProduct().getSellerId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return sellerIds.size() == 1 ? sellerIds.getFirst() : null;
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.orderedItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getVariantId(),
                        item.getSku(),
                        item.getQuantity(),
                        item.getUnitPriceAmount()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCurrencyCode(),
                items
        );
    }
}
