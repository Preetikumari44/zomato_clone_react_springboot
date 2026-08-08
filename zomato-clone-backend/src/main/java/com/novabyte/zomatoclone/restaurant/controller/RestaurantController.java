package com.novabyte.zomatoclone.restaurant.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantApprovalRequest;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantRequest;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;
import com.novabyte.zomatoclone.restaurant.service.RestaurantService;
import com.novabyte.zomatoclone.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants", description = "Public browsing, owner management, and admin approval")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ApiResponse<PagedResponse<RestaurantResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisineType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(restaurantService.searchApproved(keyword, city, cuisineType, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<RestaurantResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(restaurantService.getApprovedById(id));
    }

    @GetMapping("/owner/mine")
    public ApiResponse<PagedResponse<RestaurantResponse>> listMine(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(restaurantService.listMine(principal.userId(), pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RestaurantResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody RestaurantRequest request) {
        return ApiResponse.success("Restaurant submitted for approval", restaurantService.create(principal.userId(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RestaurantResponse> update(@AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody RestaurantRequest request) {
        return ApiResponse.success("Restaurant updated", restaurantService.update(principal.userId(), id, request));
    }

    @PostMapping(value = "/{id}/logo", consumes = "multipart/form-data")
    public ApiResponse<RestaurantResponse> uploadLogo(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable Long id,
                                                        @RequestParam("file") MultipartFile file) {
        return ApiResponse.success("Logo uploaded", restaurantService.uploadLogo(principal.userId(), id, file));
    }

    @PatchMapping("/{id}/approve")
    public ApiResponse<RestaurantResponse> approve(@PathVariable Long id) {
        return ApiResponse.success("Restaurant approved", restaurantService.approve(id));
    }

    @PatchMapping("/{id}/reject")
    public ApiResponse<RestaurantResponse> reject(@PathVariable Long id,
                                                    @Valid @RequestBody RestaurantApprovalRequest request) {
        return ApiResponse.success("Restaurant rejected", restaurantService.reject(id, request.getReason()));
    }
}
