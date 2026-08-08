package com.novabyte.zomatoclone.restaurant.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.novabyte.zomatoclone.common.enums.RestaurantStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantResponse {
    private final Long id;
    private final Long ownerId;
    private final String ownerName;
    private final String name;
    private final String description;
    private final String cuisineType;
    private final String address;
    private final String city;
    private final String logoUrl;
    private final String rejectionReason;
    private final RestaurantStatus status;
    private final BigDecimal avgRating;
    private final Instant createdAt;
}
