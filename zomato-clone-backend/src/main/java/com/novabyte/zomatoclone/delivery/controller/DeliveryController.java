package com.novabyte.zomatoclone.delivery.controller;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.novabyte.zomatoclone.common.response.ApiResponse;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.delivery.dto.DeliveryAssignmentResponse;
import com.novabyte.zomatoclone.delivery.service.DeliveryService;
import com.novabyte.zomatoclone.security.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/delivery")
@Tag(name = "Delivery", description = "Self-service pickup pool, active deliveries, and history for delivery partners")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/available")
    public ApiResponse<PagedResponse<DeliveryAssignmentResponse>> listAvailable(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(deliveryService.listAvailable(pageable));
    }

    @GetMapping("/assigned")
    public ApiResponse<PagedResponse<DeliveryAssignmentResponse>> listAssigned(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(deliveryService.listMyActive(principal.userId(), pageable));
    }

    @GetMapping("/history")
    public ApiResponse<PagedResponse<DeliveryAssignmentResponse>> listHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(deliveryService.listMyHistory(principal.userId(), pageable));
    }

    @PatchMapping("/{orderId}/accept")
    public ApiResponse<DeliveryAssignmentResponse> accept(@AuthenticationPrincipal UserPrincipal principal,
                                                             @PathVariable Long orderId) {
        return ApiResponse.success("Delivery claimed", deliveryService.accept(principal.userId(), orderId));
    }

    @PatchMapping("/{orderId}/picked-up")
    public ApiResponse<DeliveryAssignmentResponse> markPickedUp(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @PathVariable Long orderId) {
        return ApiResponse.success("Order marked picked up", deliveryService.markPickedUp(principal.userId(), orderId));
    }

    @PatchMapping("/{orderId}/delivered")
    public ApiResponse<DeliveryAssignmentResponse> markDelivered(@AuthenticationPrincipal UserPrincipal principal,
                                                                    @PathVariable Long orderId) {
        return ApiResponse.success("Order marked delivered", deliveryService.markDelivered(principal.userId(), orderId));
    }
}
