package com.novabyte.zomatoclone.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.novabyte.zomatoclone.common.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private final Long id;
    private final Long customerId;
    private final String customerName;
    private final Long restaurantId;
    private final String restaurantName;
    private final String deliveryAddress;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final List<OrderItemResponse> items;
    private final List<OrderStatusHistoryResponse> statusHistory;
    private final Instant placedAt;
    private final Instant updatedAt;
}
