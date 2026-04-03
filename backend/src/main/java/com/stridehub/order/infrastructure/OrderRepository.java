package com.stridehub.order.infrastructure;

import com.stridehub.order.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByPayment_Id(UUID paymentId);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findByIdAndUser_Id(UUID orderId, UUID userId);

    @EntityGraph(attributePaths = {"items"})
    List<Order> findAllByUser_IdOrderByCreatedAtDesc(UUID userId);
}
