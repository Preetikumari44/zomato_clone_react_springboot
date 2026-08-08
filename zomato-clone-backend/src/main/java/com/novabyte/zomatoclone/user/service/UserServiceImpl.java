package com.novabyte.zomatoclone.user.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.upload.service.CloudinaryService;
import com.novabyte.zomatoclone.user.dto.UpdateProfileRequest;
import com.novabyte.zomatoclone.user.dto.UserProfileDto;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.entity.UserRole;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final String AVATAR_FOLDER = "zomato-clone/avatars";

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public UserProfileDto getProfile(Long userId) {
        return toDto(findUser(userId));
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileDto uploadAvatar(Long userId, MultipartFile file) {
        User user = findUser(userId);
        String url = cloudinaryService.uploadImage(file, AVATAR_FOLDER);
        user.setProfileImageUrl(url);
        return toDto(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
    }

    private UserProfileDto toDto(User user) {
        Set<Role> roles = user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet());
        return UserProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(roles)
                .build();
    }
}
