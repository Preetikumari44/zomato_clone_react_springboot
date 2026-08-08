package com.novabyte.zomatoclone.security;

import com.novabyte.zomatoclone.common.enums.Role;

/**
 * What ends up as Authentication#getPrincipal() for every authenticated
 * request. Controllers/services pull this via
 * (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
 * — or more conveniently, @AuthenticationPrincipal UserPrincipal principal in a controller method.
 */
public record UserPrincipal(Long userId, String email, Role activeRole) {
}
