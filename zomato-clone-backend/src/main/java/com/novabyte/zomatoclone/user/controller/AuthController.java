package com.novabyte.zomatoclone.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.security.UserPrincipal;
import com.novabyte.zomatoclone.user.dto.AuthResponse;
import com.novabyte.zomatoclone.user.dto.LoginRequest;
import com.novabyte.zomatoclone.user.dto.RegisterRequest;
import com.novabyte.zomatoclone.user.dto.SwitchRoleRequest;
import com.novabyte.zomatoclone.user.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registration, login, and role switching")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Account created", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }

    @PostMapping("/switch-role")
    public ApiResponse<AuthResponse> switchRole(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody SwitchRoleRequest request) {
        return ApiResponse.success("Active role switched", authService.switchRole(principal, request));
    }
}
