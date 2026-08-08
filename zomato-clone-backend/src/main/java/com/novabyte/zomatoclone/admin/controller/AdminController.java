package com.novabyte.zomatoclone.admin.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.novabyte.zomatoclone.admin.dto.DashboardStatsDto;
import com.novabyte.zomatoclone.admin.dto.ManageUserResponse;
import com.novabyte.zomatoclone.admin.service.AdminService;
import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Dashboard analytics, user management, restaurant approvals, order oversight")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardStatsDto> dashboard() {
        return ApiResponse.success(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ApiResponse<PagedResponse<ManageUserResponse>> listUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(adminService.listUsers(keyword, pageable));
    }

    @PatchMapping("/users/{id}/deactivate")
    public ApiResponse<ManageUserResponse> deactivateUser(@PathVariable Long id) {
        return ApiResponse.success("User deactivated", adminService.deactivateUser(id));
    }

    @GetMapping("/restaurants/pending")
    public ApiResponse<PagedResponse<RestaurantResponse>> pendingRestaurants(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(adminService.listPendingRestaurants(pageable));
    }

    @GetMapping("/orders")
    public ApiResponse<PagedResponse<OrderResponse>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(adminService.listOrders(status, pageable));
    }
}
