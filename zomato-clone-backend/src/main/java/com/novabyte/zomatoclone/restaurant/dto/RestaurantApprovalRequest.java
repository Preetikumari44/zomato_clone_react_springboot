package com.novabyte.zomatoclone.restaurant.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Body for PATCH /api/restaurants/{id}/reject — the reason is optional but shown to the owner. */
@Getter
@Setter
public class RestaurantApprovalRequest {

    @Size(max = 500)
    private String reason;
}
