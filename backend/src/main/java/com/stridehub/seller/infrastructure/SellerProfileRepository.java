package com.stridehub.seller.infrastructure;

import com.stridehub.seller.domain.SellerProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, UUID> {

    Optional<SellerProfile> findByUser_Id(UUID userId);
}
