package com.novabyte.zomatoclone.upload.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.novabyte.zomatoclone.common.exception.BadRequestException;

/**
 * Thin wrapper around the Cloudinary SDK, shared by every module that
 * needs image upload (restaurant logos, menu item photos, user avatars).
 * Callers pass a "folder" so assets stay organized in the Cloudinary
 * dashboard (e.g. "zomato-clone/restaurants", "zomato-clone/menu-items").
 */
@Service
public class CloudinaryService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB, mirrors application.yml

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file, String folder) {
        validateImage(file);
        try {
            @SuppressWarnings("rawtypes")
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", "image"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException ex) {
            throw new BadRequestException("Image upload failed: " + ex.getMessage());
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was uploaded");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("Image must be under 5MB");
        }
    }
}
