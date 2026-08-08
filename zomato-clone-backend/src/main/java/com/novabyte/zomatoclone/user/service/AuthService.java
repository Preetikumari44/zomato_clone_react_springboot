package com.novabyte.zomatoclone.user.service;

import com.novabyte.zomatoclone.security.UserPrincipal;
import com.novabyte.zomatoclone.user.dto.AuthResponse;
import com.novabyte.zomatoclone.user.dto.LoginRequest;
import com.novabyte.zomatoclone.user.dto.RegisterRequest;
import com.novabyte.zomatoclone.user.dto.SwitchRoleRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse switchRole(UserPrincipal principal, SwitchRoleRequest request);
}
