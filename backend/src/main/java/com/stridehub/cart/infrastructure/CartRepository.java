package com.stridehub.cart.infrastructure;

import com.stridehub.cart.domain.Cart;
import com.stridehub.cart.domain.CartStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("""
            select distinct cart from Cart cart
            left join fetch cart.items items
            left join fetch items.variant variant
            left join fetch variant.product product
            where cart.user.id = :userId
            """)
    Optional<Cart> findDetailedByUserId(@Param("userId") UUID userId);

    @Query("""
            select distinct cart from Cart cart
            left join fetch cart.items items
            left join fetch items.variant variant
            left join fetch variant.product product
            where cart.user.id = :userId and cart.status = :status
            """)
    Optional<Cart> findDetailedByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") CartStatus status);
}
