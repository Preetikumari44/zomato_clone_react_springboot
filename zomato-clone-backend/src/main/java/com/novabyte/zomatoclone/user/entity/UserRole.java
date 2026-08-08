package com.novabyte.zomatoclone.user.entity;

import com.novabyte.zomatoclone.common.enums.Role;

import jakarta.persistence.*;
import lombok.*;

/**
 * Junction row: one user can hold several of these, which is how a single
 * account supports e.g. CUSTOMER + DELIVERY_PARTNER simultaneously and
 * switches between them post-login (see AuthService#switchRole).
 */
@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;
}
