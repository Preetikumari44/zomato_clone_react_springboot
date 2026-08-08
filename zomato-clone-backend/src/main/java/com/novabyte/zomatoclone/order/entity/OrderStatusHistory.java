package com.novabyte.zomatoclone.order.entity;

import java.time.Instant;

import com.novabyte.zomatoclone.common.enums.OrderStatus;
import com.novabyte.zomatoclone.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

/** Append-only audit trail — every status transition gets a row, never updated or deleted. */
@Entity
@Table(name = "order_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @PrePersist
    void onCreate() {
        changedAt = Instant.now();
    }
}
