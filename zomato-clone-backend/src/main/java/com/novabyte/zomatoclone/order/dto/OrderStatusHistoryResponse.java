package com.novabyte.zomatoclone.order.dto;

import java.time.Instant;

import com.novabyte.zomatoclone.common.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderStatusHistoryResponse {
    private final OrderStatus status;
    private final Instant changedAt;
}
