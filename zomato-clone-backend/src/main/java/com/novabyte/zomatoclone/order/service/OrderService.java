package com.novabyte.zomatoclone.order.service;

import org.springframework.data.domain.Pageable;

import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.order.dto.OrderStatusUpdateRequest;
import com.novabyte.zomatoclone.order.dto.PlaceOrderRequest;
import com.novabyte.zomatoclone.security.UserPrincipal;

public interface OrderService {

    OrderResponse placeOrder(Long customerId, PlaceOrderRequest request);

    OrderResponse getOrder(UserPrincipal principal, Long orderId);

    PagedResponse<OrderResponse> listMine(Long customerId, Pageable pageable);

    PagedResponse<OrderResponse> listForRestaurant(Long ownerId, Long restaurantId, Pageable pageable);

    OrderResponse accept(Long ownerId, Long orderId);

    OrderResponse reject(Long ownerId, Long orderId);

    OrderResponse updateStatus(Long ownerId, Long orderId, OrderStatusUpdateRequest request);
}
