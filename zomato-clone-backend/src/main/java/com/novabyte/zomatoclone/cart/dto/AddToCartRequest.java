package com.novabyte.zomatoclone.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "menuItemId is required")
    private Long menuItemId;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * If the cart already holds items from a different restaurant, the add
     * is rejected with a 400 UNLESS this is true — mirrors the real Zomato
     * "your cart has items from another restaurant, start a new cart?"
     * confirmation instead of silently discarding items.
     */
    private boolean replaceCart = false;
}
