package com.novabyte.zomatoclone.restaurant.service;

import org.springframework.data.domain.Pageable;

import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantRequest;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;

public interface RestaurantService {

    RestaurantResponse create(Long ownerId, RestaurantRequest request);

    RestaurantResponse update(Long ownerId, Long restaurantId, RestaurantRequest request);

    RestaurantResponse uploadLogo(Long ownerId, Long restaurantId, MultipartFile file);

    RestaurantResponse getApprovedById(Long id);

    PagedResponse<RestaurantResponse> searchApproved(String keyword, String city, String cuisineType, Pageable pageable);

    PagedResponse<RestaurantResponse> listMine(Long ownerId, Pageable pageable);

    RestaurantResponse approve(Long restaurantId);

    RestaurantResponse reject(Long restaurantId, String reason);
}
