package com.novabyte.zomatoclone.user.dto;

import java.util.Set;

import com.novabyte.zomatoclone.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileDto {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String profileImageUrl;
    private final Set<Role> roles;
}
