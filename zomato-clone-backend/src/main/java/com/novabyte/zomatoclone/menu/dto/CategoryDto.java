package com.novabyte.zomatoclone.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryDto {
    private final Long id;
    private final Long restaurantId;
    private final String name;
}
