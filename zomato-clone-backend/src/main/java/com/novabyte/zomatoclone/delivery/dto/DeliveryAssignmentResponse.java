package com.novabyte.zomatoclone.delivery.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.novabyte.zomatoclone.common.enums.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryAssignmentResponse {
    private final Long assignmentId;
    private final Long orderId;
    private final String restaurantName;
    private final String restaurantAddress;
    private final String deliveryAddress;
    private final BigDecimal totalAmount;
    private final DeliveryStatus status;
    private final Instant assignedAt;
    private final Instant pickedUpAt;
    private final Instant deliveredAt;
}
