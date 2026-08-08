package com.novabyte.zomatoclone.menu.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    /** Optional — null means "uncategorized". Must belong to the same restaurant. */
    private Long categoryId;

    @NotNull
    private Boolean veg;

    @NotNull
    private Boolean available;
}
