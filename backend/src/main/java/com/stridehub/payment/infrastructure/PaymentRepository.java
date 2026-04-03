package com.stridehub.payment.infrastructure;

import com.stridehub.payment.domain.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @EntityGraph(attributePaths = {"checkoutSession", "checkoutSession.user"})
    Optional<Payment> findByCheckoutSession_Id(UUID checkoutSessionId);

    Optional<Payment> findByProviderPaymentRef(String providerPaymentRef);
}
