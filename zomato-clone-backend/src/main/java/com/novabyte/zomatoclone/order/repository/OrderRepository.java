package com.novabyte.zomatoclone.order.repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerIdOrderByPlacedAtDesc(Long customerId, Pageable pageable);

    Page<Order> findByRestaurantIdOrderByPlacedAtDesc(Long restaurantId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE :status IS NULL OR o.status = :status ORDER BY o.placedAt DESC")
    Page<Order> findAllFiltered(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal sumDeliveredRevenue();
}
