package com.stridehub.seller.application;

import com.stridehub.common.exception.ConflictException;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.identity.domain.User;
import com.stridehub.identity.infrastructure.UserRepository;
import com.stridehub.seller.domain.SellerApplication;
import com.stridehub.seller.infrastructure.SellerApplicationRepository;
import com.stridehub.seller.web.SellerApplicationResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerApplicationService {

    private final SellerApplicationRepository sellerApplicationRepository;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    public SellerApplicationService(
            SellerApplicationRepository sellerApplicationRepository,
            UserRepository userRepository,
            TimeProvider timeProvider
    ) {
        this.sellerApplicationRepository = sellerApplicationRepository;
        this.userRepository = userRepository;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public SellerApplicationResponse submit(UUID userId, String storeName, String legalName) {
        if (sellerApplicationRepository.findByUser_Id(userId).isPresent()) {
            throw new ConflictException(
                    "seller.application_conflict",
                    "Seller application already exists for this user"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("identity.user_not_found", "User was not found"));

        SellerApplication application = new SellerApplication(
                UUID.randomUUID(),
                user,
                storeName,
                legalName,
                timeProvider.now()
        );
        return toResponse(sellerApplicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public SellerApplicationResponse getCurrent(UUID userId) {
        return sellerApplicationRepository.findByUser_Id(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "seller.application_not_found",
                        "Seller application was not found"
                ));
    }

    private SellerApplicationResponse toResponse(SellerApplication application) {
        return new SellerApplicationResponse(
                application.getId(),
                application.getStoreName(),
                application.getLegalName(),
                application.getStatus().name(),
                application.getSubmittedAt(),
                application.getReviewedAt(),
                application.getRejectionReason()
        );
    }
}
