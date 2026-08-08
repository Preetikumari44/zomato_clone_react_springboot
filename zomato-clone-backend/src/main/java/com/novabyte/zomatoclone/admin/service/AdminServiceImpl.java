package com.novabyte.zomatoclone.admin.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.novabyte.zomatoclone.admin.dto.DashboardStatsDto;
import com.novabyte.zomatoclone.admin.dto.ManageUserResponse;
import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.common.enums.Role;
import com.novabyte.zomatoclone.common.exception.ResourceNotFoundException;
import com.novabyte.zomatoclone.common.response.PagedResponse;
import com.novabyte.zomatoclone.order.dto.OrderItemResponse;
import com.novabyte.zomatoclone.order.dto.OrderResponse;
import com.novabyte.zomatoclone.order.entity.Order;
import com.novabyte.zomatoclone.order.repository.OrderRepository;
import com.novabyte.zomatoclone.restaurant.dto.RestaurantResponse;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.restaurant.repository.RestaurantRepository;
import com.novabyte.zomatoclone.user.entity.User;
import com.novabyte.zomatoclone.user.entity.UserRole;
import com.novabyte.zomatoclone.user.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(UserRepository userRepository,
                             RestaurantRepository restaurantRepository,
                             OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status.name(), orderRepository.findAllFiltered(status, Pageable.unpaged()).getTotalElements());
        }

        return DashboardStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalCustomers(userRepository.countByRole(Role.CUSTOMER))
                .totalRestaurantOwners(userRepository.countByRole(Role.RESTAURANT_OWNER))
                .totalDeliveryPartners(userRepository.countByRole(Role.DELIVERY_PARTNER))
                .totalRestaurants(restaurantRepository.count())
                .pendingRestaurants(restaurantRepository.countByStatus(RestaurantStatus.PENDING))
                .approvedRestaurants(restaurantRepository.countByStatus(RestaurantStatus.APPROVED))
                .totalOrders(orderRepository.count())
                .ordersByStatus(ordersByStatus)
                .totalRevenue(orderRepository.sumDeliveredRevenue())
                .build();
    }

    @Override
    public PagedResponse<ManageUserResponse> listUsers(String keyword, Pageable pageable) {
        String kw = StringUtils.hasText(keyword) ? keyword : null;
        Page<User> page = userRepository.search(kw, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    @Transactional
    public ManageUserResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        user.setActive(false);
        return toDto(userRepository.save(user));
    }

    @Override
    public PagedResponse<RestaurantResponse> listPendingRestaurants(Pageable pageable) {
        Page<Restaurant> page = restaurantRepository.findByStatus(RestaurantStatus.PENDING, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    @Override
    public PagedResponse<OrderResponse> listOrders(OrderStatus status, Pageable pageable) {
        Page<Order> page = orderRepository.findAllFiltered(status, pageable);
        return new PagedResponse<>(page.map(this::toDto));
    }

    // ---- mapping (kept local/duplicated rather than reaching into other
    // modules' services, so the admin view can diverge from the owner/customer
    // view of the same entity without coupling the two) ----

    private ManageUserResponse toDto(User u) {
        Set<Role> roles = u.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet());
        return ManageUserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .roles(roles)
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }

    private RestaurantResponse toDto(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .ownerId(r.getOwner().getId())
                .ownerName(r.getOwner().getFullName())
                .name(r.getName())
                .description(r.getDescription())
                .cuisineType(r.getCuisineType())
                .address(r.getAddress())
                .city(r.getCity())
                .logoUrl(r.getLogoUrl())
                .rejectionReason(r.getRejectionReason())
                .status(r.getStatus())
                .avgRating(r.getAvgRating())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private OrderResponse toDto(Order o) {
        var items = o.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .menuItemId(i.getMenuItem().getId())
                        .name(i.getItemNameSnapshot())
                        .price(i.getPriceSnapshot())
                        .quantity(i.getQuantity())
                        .subtotal(i.getPriceSnapshot().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(o.getId())
                .customerId(o.getCustomer().getId())
                .customerName(o.getCustomer().getFullName())
                .restaurantId(o.getRestaurant().getId())
                .restaurantName(o.getRestaurant().getName())
                .deliveryAddress(o.getDeliveryAddress())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .items(items)
                .statusHistory(null) // admin list view omits the full timeline; GET /api/orders/{id} has it
                .placedAt(o.getPlacedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
