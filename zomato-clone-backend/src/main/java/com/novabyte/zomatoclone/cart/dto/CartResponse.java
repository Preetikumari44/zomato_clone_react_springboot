package com.novabyte.zomatoclone.cart.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartResponse {
    private final Long cartId;
    private final Long restaurantId;
    private final String restaurantName;
    private final List<CartItemResponse> items;
    private final int itemCount;
    private final BigDecimal totalAmount;
}
