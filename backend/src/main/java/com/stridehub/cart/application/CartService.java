package com.stridehub.cart.application;

import com.stridehub.cart.domain.Cart;
import com.stridehub.cart.domain.CartItem;
import com.stridehub.cart.domain.CartStatus;
import com.stridehub.cart.infrastructure.CartRepository;
import com.stridehub.cart.infrastructure.CartProductVariantRepository;
import com.stridehub.cart.web.CartItemResponse;
import com.stridehub.cart.web.CartResponse;
import com.stridehub.catalog.domain.Product;
import com.stridehub.catalog.domain.ProductStatus;
import com.stridehub.catalog.domain.ProductVariant;
import com.stridehub.catalog.domain.ProductVariantStatus;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.identity.domain.User;
import com.stridehub.identity.infrastructure.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartProductVariantRepository productVariantRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getCurrentCart(UUID userId) {
        return toResponse(loadOrCreateActiveCart(userId));
    }

    @Transactional
    public CartResponse addItem(UUID userId, UUID variantId, int quantity) {
        Cart cart = loadOrCreateActiveCart(userId);
        ProductVariant variant = loadPurchasableVariant(variantId);
        cart.addItem(variant, quantity);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(UUID userId, UUID itemId, int quantity) {
        Cart cart = loadExistingActiveCart(userId);
        CartItem item = cart.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("cart.item_not_found", "Cart item was not found"));
        item.changeQuantity(quantity);
        item.refreshUnitPrice(item.getVariant().getPriceAmount());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(UUID userId, UUID itemId) {
        Cart cart = loadExistingActiveCart(userId);
        cart.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("cart.item_not_found", "Cart item was not found"));
        cart.removeItem(itemId);
        return toResponse(cartRepository.save(cart));
    }

    private Cart loadOrCreateActiveCart(UUID userId) {
        return cartRepository.findDetailedByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(new Cart(UUID.randomUUID(), loadUser(userId))));
    }

    private Cart loadExistingActiveCart(UUID userId) {
        return cartRepository.findDetailedByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("cart.item_not_found", "Cart item was not found"));
    }

    private ProductVariant loadPurchasableVariant(UUID variantId) {
        ProductVariant variant = productVariantRepository.findDetailedById(variantId)
                .orElseThrow(() -> new NotFoundException("cart.variant_not_found", "Variant was not found"));
        Product product = variant.getProduct();
        if (variant.getStatus() != ProductVariantStatus.ACTIVE || product.getStatus() != ProductStatus.ACTIVE) {
            throw new NotFoundException("cart.variant_not_found", "Variant was not found");
        }
        return variant;
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("identity.user_not_found", "User was not found"));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.orderedItems().stream()
                .map(this::toItemResponse)
                .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal subtotalAmount = items.stream()
                .map(CartItemResponse::lineTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currencyCode = items.isEmpty()
                ? "USD"
                : items.getFirst().currencyCode();

        return new CartResponse(
                cart.getId(),
                cart.getStatus().name(),
                currencyCode,
                totalItems,
                subtotalAmount,
                items
        );
    }

    private CartItemResponse toItemResponse(CartItem item) {
        ProductVariant variant = item.getVariant();
        Product product = variant.getProduct();
        BigDecimal unitPriceAmount = item.getUnitPriceAmount();
        BigDecimal lineTotalAmount = unitPriceAmount.multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                variant.getId(),
                variant.getSku(),
                variant.getSizeValue(),
                variant.getColorValue(),
                product.getCurrencyCode(),
                item.getQuantity(),
                unitPriceAmount,
                lineTotalAmount
        );
    }
}
