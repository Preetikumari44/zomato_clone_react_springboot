package com.novabyte.zomatoclone.order.entity;

import java.math.BigDecimal;

import com.novabyte.zomatoclone.menu.entity.MenuItem;

import jakarta.persistence.*;
import lombok.*;

/**
 * name/price are SNAPSHOTTED at order-placement time, not joined live to
 * MenuItem — so if the restaurant edits a price tomorrow, this order's
 * total stays accurate to what the customer actually paid.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "item_name_snapshot", nullable = false, length = 150)
    private String itemNameSnapshot;

    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSnapshot;

    @Column(nullable = false)
    private int quantity;
}
