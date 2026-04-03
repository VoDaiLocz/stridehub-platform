package com.stridehub.cart.web;

import com.stridehub.cart.application.CartService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse currentCart(JwtAuthenticationToken authentication) {
        return cartService.getCurrentCart(currentUserId(authentication));
    }

    @PostMapping("/items")
    public CartResponse addItem(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody CartItemUpsertRequest request
    ) {
        return cartService.addItem(currentUserId(authentication), request.variantId(), request.quantity());
    }

    @PatchMapping("/items/{itemId}")
    public CartResponse updateItemQuantity(
            JwtAuthenticationToken authentication,
            @PathVariable UUID itemId,
            @Valid @RequestBody CartItemQuantityUpdateRequest request
    ) {
        return cartService.updateItemQuantity(currentUserId(authentication), itemId, request.quantity());
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            JwtAuthenticationToken authentication,
            @PathVariable UUID itemId
    ) {
        return cartService.removeItem(currentUserId(authentication), itemId);
    }

    private UUID currentUserId(JwtAuthenticationToken authentication) {
        return UUID.fromString(authentication.getToken().getSubject());
    }
}
