package com.stridehub.checkout.infrastructure;

import com.stridehub.checkout.domain.CheckoutSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, UUID> {

    Optional<CheckoutSession> findByCart_Id(UUID cartId);

    @Query("""
            select checkoutSession from CheckoutSession checkoutSession
            join fetch checkoutSession.cart cart
            join fetch checkoutSession.user user
            where checkoutSession.id = :checkoutSessionId
            """)
    Optional<CheckoutSession> findDetailedById(UUID checkoutSessionId);
}
