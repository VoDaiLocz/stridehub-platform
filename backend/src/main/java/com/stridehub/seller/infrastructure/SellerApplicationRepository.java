package com.stridehub.seller.infrastructure;

import com.stridehub.seller.domain.SellerApplication;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, UUID> {

    Optional<SellerApplication> findByUser_Id(UUID userId);
}
