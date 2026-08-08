package com.novabyte.zomatoclone.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novabyte.zomatoclone.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
