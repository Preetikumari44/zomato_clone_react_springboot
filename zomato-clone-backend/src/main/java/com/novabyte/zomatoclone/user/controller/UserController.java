package com.novabyte.zomatoclone.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.security.UserPrincipal;
import com.novabyte.zomatoclone.user.dto.UpdateProfileRequest;
import com.novabyte.zomatoclone.user.dto.UserProfileDto;
import com.novabyte.zomatoclone.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Authenticated user's own profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileDto> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(userService.getProfile(principal.userId()));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileDto> updateMyProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                         @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success("Profile updated", userService.updateProfile(principal.userId(), request));
    }

    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    public ApiResponse<UserProfileDto> uploadAvatar(@AuthenticationPrincipal UserPrincipal principal,
                                                      @RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Avatar updated", userService.uploadAvatar(principal.userId(), file));
    }
}
