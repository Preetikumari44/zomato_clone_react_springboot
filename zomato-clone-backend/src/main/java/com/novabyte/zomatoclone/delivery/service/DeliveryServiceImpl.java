package com.novabyte.zomatoclone.delivery.service;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.novabyte.zomatoclone.common.enums.DeliveryStatus;
import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.common.exception.BadRequestException;
import com.novabyte.zomatoclone.common.exception.ForbiddenOperationException;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.delivery.dto.DeliveryAssignmentResponse;
import com.novabyte.zomatoclone.delivery.entity.DeliveryAssignment;
import com.novabyte.zomatoclone.delivery.entity.DeliveryPartnerProfile;
import com.novabyte.zomatoclone.delivery.repository.DeliveryAssignmentRepository;
import com.novabyte.zomatoclone.delivery.repository.DeliveryPartnerProfileRepository;
import com.novabyte.zomatoclone.order.entity.Order;
import com.novabyte.zomatoclone.order.entity.OrderStatusHistory;
import com.novabyte.zomatoclone.order.repository.OrderRepository;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryAssignmentRepository assignmentRepository;
    private final DeliveryPartnerProfileRepository profileRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DeliveryServiceImpl(DeliveryAssignmentRepository assignmentRepository,
                                DeliveryPartnerProfileRepository profileRepository,
                                OrderRepository orderRepository,
                                UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.profileRepository = profileRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PagedResponse<DeliveryAssignmentResponse> listAvailable(Pageable pageable) {
        Page<DeliveryAssignment> page = assignmentRepository.findAvailableForPickup(pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponse accept(Long partnerUserId, Long orderId) {
        DeliveryPartnerProfile profile = findOrCreateProfile(partnerUserId);
        Order order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BadRequestException("Order is not ready for pickup yet");
        }

        DeliveryAssignment assignment = findOrCreateAssignment(order);
        if (assignment.getStatus() != DeliveryStatus.UNASSIGNED) {
            throw new BadRequestException("This delivery has already been claimed by another partner");
        }

        assignment.setDeliveryPartner(profile);
        assignment.setStatus(DeliveryStatus.ASSIGNED);
        assignment.setAssignedAt(Instant.now());
        profile.setAvailable(false);

        profileRepository.save(profile);
        return toDto(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponse markPickedUp(Long partnerUserId, Long orderId) {
        DeliveryAssignment assignment = findOwnedAssignment(partnerUserId, orderId);
        requireAssignmentStatus(assignment, DeliveryStatus.ASSIGNED, DeliveryStatus.PICKED_UP);

        assignment.setStatus(DeliveryStatus.PICKED_UP);
        assignment.setPickedUpAt(Instant.now());

        advanceOrderStatus(assignment.getOrder(), OrderStatus.PICKED_UP);

        return toDto(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponse markDelivered(Long partnerUserId, Long orderId) {
        DeliveryAssignment assignment = findOwnedAssignment(partnerUserId, orderId);
        requireAssignmentStatus(assignment, DeliveryStatus.PICKED_UP, DeliveryStatus.DELIVERED);

        assignment.setStatus(DeliveryStatus.DELIVERED);
        assignment.setDeliveredAt(Instant.now());

        advanceOrderStatus(assignment.getOrder(), OrderStatus.DELIVERED);

        DeliveryPartnerProfile profile = assignment.getDeliveryPartner();
        profile.setAvailable(true);
        profileRepository.save(profile);

        return toDto(assignmentRepository.save(assignment));
    }

    @Override
    public PagedResponse<DeliveryAssignmentResponse> listMyActive(Long partnerUserId, Pageable pageable) {
        DeliveryPartnerProfile profile = findOrCreateProfile(partnerUserId);
        Page<DeliveryAssignment> page = assignmentRepository.findByDeliveryPartnerIdAndStatusIn(
                profile.getId(), List.of(DeliveryStatus.ASSIGNED, DeliveryStatus.PICKED_UP), pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    public PagedResponse<DeliveryAssignmentResponse> listMyHistory(Long partnerUserId, Pageable pageable) {
        DeliveryPartnerProfile profile = findOrCreateProfile(partnerUserId);
        Page<DeliveryAssignment> page = assignmentRepository.findByDeliveryPartnerIdAndStatus(
                profile.getId(), DeliveryStatus.DELIVERED, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    // ---- helpers ----

    private DeliveryPartnerProfile findOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
            return profileRepository.save(DeliveryPartnerProfile.builder().user(user).build());
        });
    }

    /** One assignment row per order, created on first touch (see DeliveryAssignment's class comment). */
    private DeliveryAssignment findOrCreateAssignment(Order order) {
        return assignmentRepository.findByOrderId(order.getId())
                .orElseGet(() -> assignmentRepository.save(
                        DeliveryAssignment.builder().order(order).status(DeliveryStatus.UNASSIGNED).build()));
    }

    private DeliveryAssignment findOwnedAssignment(Long partnerUserId, Long orderId) {
        DeliveryPartnerProfile profile = findOrCreateProfile(partnerUserId);
        DeliveryAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Delivery assignment for order", orderId));

        if (assignment.getDeliveryPartner() == null
                || !assignment.getDeliveryPartner().getId().equals(profile.getId())) {
            throw new ForbiddenOperationException("This delivery is not assigned to you");
        }
        return assignment;
    }

    private void requireAssignmentStatus(DeliveryAssignment assignment, DeliveryStatus expected, DeliveryStatus target) {
        if (assignment.getStatus() != expected) {
            throw new BadRequestException(
                    "Cannot move delivery from " + assignment.getStatus() + " to " + target +
                    " (expected current status " + expected + ")");
        }
    }

    /** Keeps Order's own status + audit trail in sync with the delivery side of the lifecycle. */
    private void advanceOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        order.getStatusHistory().add(OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .changedBy(null) // system-driven transition, triggered by the delivery partner's action
                .build());
        orderRepository.save(order);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
    }

    private DeliveryAssignmentResponse toDto(DeliveryAssignment a) {
        Order order = a.getOrder();
        return DeliveryAssignmentResponse.builder()
                .assignmentId(a.getId())
                .orderId(order.getId())
                .restaurantName(order.getRestaurant().getName())
                .restaurantAddress(order.getRestaurant().getAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .status(a.getStatus())
                .assignedAt(a.getAssignedAt())
                .pickedUpAt(a.getPickedUpAt())
                .deliveredAt(a.getDeliveredAt())
                .build();
    }
}
