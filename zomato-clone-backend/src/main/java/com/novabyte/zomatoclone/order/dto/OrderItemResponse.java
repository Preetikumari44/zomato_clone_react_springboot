package com.novabyte.zomatoclone.order.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    private final Long menuItemId;
    private final String name;
    private final BigDecimal price;
    private final int quantity;
    private final BigDecimal subtotal;
}
