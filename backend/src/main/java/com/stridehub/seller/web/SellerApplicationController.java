package com.stridehub.seller.web;

import com.stridehub.seller.application.SellerApplicationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/application")
public class SellerApplicationController {

    private final SellerApplicationService sellerApplicationService;

    public SellerApplicationController(SellerApplicationService sellerApplicationService) {
        this.sellerApplicationService = sellerApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerApplicationResponse submit(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody SellerApplicationRequest request
    ) {
        return sellerApplicationService.submit(currentUserId(authentication), request.storeName(), request.legalName());
    }

    @GetMapping
    public SellerApplicationResponse getCurrent(JwtAuthenticationToken authentication) {
        return sellerApplicationService.getCurrent(currentUserId(authentication));
    }

    private UUID currentUserId(JwtAuthenticationToken authentication) {
        return UUID.fromString(authentication.getToken().getSubject());
    }
}
