package com.novabyte.zomatoclone.delivery.entity;

import com.novabyte.zomatoclone.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_partner_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPartnerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private boolean available = true;
}
