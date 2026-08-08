package com.novabyte.zomatoclone.user.dto;

import com.novabyte.zomatoclone.common.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwitchRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
