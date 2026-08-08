package com.novabyte.zomatoclone.delivery.service;

import org.springframework.data.domain.Pageable;

import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.delivery.dto.DeliveryAssignmentResponse;

public interface DeliveryService {

    PagedResponse<DeliveryAssignmentResponse> listAvailable(Pageable pageable);

    DeliveryAssignmentResponse accept(Long partnerUserId, Long orderId);

    DeliveryAssignmentResponse markPickedUp(Long partnerUserId, Long orderId);

    DeliveryAssignmentResponse markDelivered(Long partnerUserId, Long orderId);

    PagedResponse<DeliveryAssignmentResponse> listMyActive(Long partnerUserId, Pageable pageable);

    PagedResponse<DeliveryAssignmentResponse> listMyHistory(Long partnerUserId, Pageable pageable);
}
