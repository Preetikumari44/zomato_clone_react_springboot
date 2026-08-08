package com.novabyte.zomatoclone.user.dto;

import java.util.Set;

import com.novabyte.zomatoclone.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private final String token;
    @Builder.Default
    private final String tokenType = "Bearer";
    private final Long userId;
    private final String fullName;
    private final String email;
    private final Role activeRole;
    private final Set<Role> availableRoles;
}
