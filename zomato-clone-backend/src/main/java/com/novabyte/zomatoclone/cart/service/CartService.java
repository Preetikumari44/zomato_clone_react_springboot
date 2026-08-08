package com.novabyte.zomatoclone.cart.service;

import com.novabyte.zomatoclone.cart.dto.AddToCartRequest;
import com.novabyte.zomatoclone.cart.dto.CartResponse;
import com.novabyte.zomatoclone.cart.dto.UpdateCartItemRequest;

public interface CartService {
    CartResponse getMyCart(Long customerId);
    CartResponse addItem(Long customerId, AddToCartRequest request);
    CartResponse updateItemQuantity(Long customerId, Long menuItemId, UpdateCartItemRequest request);
    CartResponse removeItem(Long customerId, Long menuItemId);
    void clearCart(Long customerId);
}
