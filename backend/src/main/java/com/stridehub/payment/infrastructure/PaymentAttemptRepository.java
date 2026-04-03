package com.stridehub.payment.infrastructure;

import com.stridehub.payment.domain.PaymentAttempt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID> {

    boolean existsByPayment_IdAndProviderRequestId(UUID paymentId, String providerRequestId);
}
