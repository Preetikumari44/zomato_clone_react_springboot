package com.novabyte.zomatoclone.admin.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DashboardStatsDto {
    private final long totalUsers;
    private final long totalCustomers;
    private final long totalRestaurantOwners;
    private final long totalDeliveryPartners;
    private final long totalRestaurants;
    private final long pendingRestaurants;
    private final long approvedRestaurants;
    private final long totalOrders;
    private final Map<String, Long> ordersByStatus;
    private final BigDecimal totalRevenue;
}
