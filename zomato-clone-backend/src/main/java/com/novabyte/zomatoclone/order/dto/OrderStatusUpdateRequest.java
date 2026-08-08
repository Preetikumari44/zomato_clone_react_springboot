package com.novabyte.zomatoclone.order.dto;

import com.novabyte.zomatoclone.common.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Only PREPARING and READY_FOR_PICKUP are valid here — see OrderServiceImpl#updateStatus. */
@Getter
@Setter
public class OrderStatusUpdateRequest {

    @NotNull
    private OrderStatus status;
}
