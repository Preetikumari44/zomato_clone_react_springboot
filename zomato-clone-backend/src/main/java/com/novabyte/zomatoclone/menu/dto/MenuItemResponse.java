package com.novabyte.zomatoclone.menu.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuItemResponse {
    private final Long id;
    private final Long restaurantId;
    private final Long categoryId;
    private final String categoryName;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String imageUrl;
    private final boolean veg;
    private final boolean available;
    private final Instant createdAt;
}
