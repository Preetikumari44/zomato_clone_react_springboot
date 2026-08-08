package com.novabyte.zomatoclone.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.novabyte.zomatoclone.cart.dto.AddToCartRequest;
import com.novabyte.zomatoclone.cart.dto.CartResponse;
import com.novabyte.zomatoclone.cart.dto.UpdateCartItemRequest;
import com.novabyte.zomatoclone.cart.service.CartService;
import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "The authenticated customer's single-restaurant cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> getMyCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(cartService.getMyCart(principal.userId()));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartResponse> addItem(@AuthenticationPrincipal UserPrincipal principal,
                                              @Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success("Item added to cart", cartService.addItem(principal.userId(), request));
    }

    @PutMapping("/items/{menuItemId}")
    public ApiResponse<CartResponse> updateItem(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable Long menuItemId,
                                                 @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success("Quantity updated", cartService.updateItemQuantity(principal.userId(), menuItemId, request));
    }

    @DeleteMapping("/items/{menuItemId}")
    public ApiResponse<CartResponse> removeItem(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable Long menuItemId) {
        return ApiResponse.success("Item removed", cartService.removeItem(principal.userId(), menuItemId));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.userId());
    }
}
