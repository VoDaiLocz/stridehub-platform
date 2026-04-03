package com.stridehub.seller.web;

import jakarta.validation.constraints.NotBlank;

public record SellerApplicationRequest(
        @NotBlank(message = "storeName must not be blank")
        String storeName,
        String legalName
) {
}
