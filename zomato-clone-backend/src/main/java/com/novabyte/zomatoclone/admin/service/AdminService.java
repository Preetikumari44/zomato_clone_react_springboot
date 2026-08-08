package com.novabyte.zomatoclone.admin.service;

import org.springframework.data.domain.Pageable;

import com.novabyte.zomatoclone.admin.dto.DashboardStatsDto;
import com.novabyte.zomatoclone.admin.dto.ManageUserResponse;
import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;

public interface AdminService {

    DashboardStatsDto getDashboardStats();

    PagedResponse<ManageUserResponse> listUsers(String keyword, Pageable pageable);

    ManageUserResponse deactivateUser(Long userId);

    PagedResponse<RestaurantResponse> listPendingRestaurants(Pageable pageable);

    PagedResponse<OrderResponse> listOrders(OrderStatus status, Pageable pageable);
}
