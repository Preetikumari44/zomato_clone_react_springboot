package com.novabyte.zomatoclone.cart.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartItemResponse {
    private final Long cartItemId;
    private final Long menuItemId;
    private final String name;
    private final BigDecimal price;
    private final String imageUrl;
    private final boolean veg;
    private final int quantity;
    private final BigDecimal subtotal;
}
