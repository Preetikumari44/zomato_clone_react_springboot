package com.novabyte.zomatoclone.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 150)
    private String fullName;

    @Pattern(regexp = "^$|^[0-9+ -]{7,20}$", message = "Phone number is invalid")
    private String phone;
}
