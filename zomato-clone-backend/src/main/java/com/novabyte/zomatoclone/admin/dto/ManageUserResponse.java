package com.novabyte.zomatoclone.admin.dto;

import java.time.Instant;
import java.util.Set;

import com.novabyte.zomatoclone.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ManageUserResponse {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final Set<Role> roles;
    private final boolean active;
    private final Instant createdAt;
}
