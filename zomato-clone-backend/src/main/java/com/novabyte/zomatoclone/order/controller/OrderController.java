package com.novabyte.zomatoclone.order.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.order.dto.OrderStatusUpdateRequest;
import com.novabyte.zomatoclone.order.dto.PlaceOrderRequest;
import com.novabyte.zomatoclone.order.service.OrderService;
import com.novabyte.zomatoclone.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Placement, tracking, and the restaurant-side order lifecycle")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> placeOrder(@AuthenticationPrincipal UserPrincipal principal,
                                                  @Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.success("Order placed", orderService.placeOrder(principal.userId(), request));
    }

    @GetMapping("/mine")
    public ApiResponse<PagedResponse<OrderResponse>> listMine(@AuthenticationPrincipal UserPrincipal principal,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(orderService.listMine(principal.userId(), pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@AuthenticationPrincipal UserPrincipal principal,
                                               @PathVariable Long id) {
        return ApiResponse.success(orderService.getOrder(principal, id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ApiResponse<PagedResponse<OrderResponse>> listForRestaurant(@AuthenticationPrincipal UserPrincipal principal,
                                                                         @PathVariable Long restaurantId,
                                                                         @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(orderService.listForRestaurant(principal.userId(), restaurantId, pageable));
    }

    @PatchMapping("/{id}/accept")
    public ApiResponse<OrderResponse> accept(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.success("Order accepted", orderService.accept(principal.userId(), id));
    }

    @PatchMapping("/{id}/reject")
    public ApiResponse<OrderResponse> reject(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.success("Order rejected", orderService.reject(principal.userId(), id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateStatus(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.success("Order status updated", orderService.updateStatus(principal.userId(), id, request));
    }
}
