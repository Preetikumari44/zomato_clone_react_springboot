package com.novabyte.zomatoclone.delivery.entity;

import java.time.Instant;

import com.novabyte.zomatoclone.common.enums.DeliveryStatus;
import com.novabyte.zomatoclone.order.entity.Order;

import jakarta.persistence.*;
import lombok.*;

/**
 * Created lazily — the first time a delivery partner acts on an order
 * (see DeliveryServiceImpl#findOrCreateAssignment) — rather than at order
 * placement, since most orders won't need one until they reach
 * READY_FOR_PICKUP. `order` stays UNIQUE either way: at most one
 * assignment ever exists per order.
 */
@Entity
@Table(name = "delivery_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartnerProfile deliveryPartner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.UNASSIGNED;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "picked_up_at")
    private Instant pickedUpAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;
}
