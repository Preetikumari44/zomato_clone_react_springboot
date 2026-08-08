package com.novabyte.zomatoclone.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @Size(max = 100)
    private String cuisineType;

    @NotBlank(message = "Address is required")
    @Size(max = 255)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;
}
