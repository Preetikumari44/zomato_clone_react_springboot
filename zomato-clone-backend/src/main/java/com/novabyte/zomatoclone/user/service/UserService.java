package com.novabyte.zomatoclone.user.service;

import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.user.dto.UpdateProfileRequest;
import com.novabyte.zomatoclone.user.dto.UserProfileDto;

public interface UserService {
    UserProfileDto getProfile(Long userId);
    UserProfileDto updateProfile(Long userId, UpdateProfileRequest request);
    UserProfileDto uploadAvatar(Long userId, MultipartFile file);
}
