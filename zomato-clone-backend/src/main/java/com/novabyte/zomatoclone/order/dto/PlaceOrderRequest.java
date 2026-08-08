package com.novabyte.zomatoclone.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Orders are placed FROM the customer's current cart — no item list here, just where it's going. */
@Getter
@Setter
public class PlaceOrderRequest {

    @NotBlank(message = "Delivery address is required")
    @Size(max = 255)
    private String deliveryAddress;
}
