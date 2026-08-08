package com.novabyte.zomatoclone.user.dto;

import java.util.Set;

import com.novabyte.zomatoclone.common.enums.Role;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^$|^[0-9+ -]{7,20}$", message = "Phone number is invalid")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Roles the user wants at signup, e.g. {CUSTOMER} or {CUSTOMER, DELIVERY_PARTNER}.
     * ADMIN is silently rejected here regardless of what's sent — see AuthServiceImpl.
     */
    @NotEmpty(message = "At least one role is required")
    private Set<Role> roles;
}
