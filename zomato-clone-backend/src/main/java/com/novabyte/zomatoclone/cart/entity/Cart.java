package com.novabyte.zomatoclone.cart.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.novabyte.zomatoclone.restaurant.entity.Restaurant;
import com.novabyte.zomatoclone.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

/**
 * One cart per customer (unique on customer_id), and single-restaurant —
 * `restaurant` is null while the cart is empty and gets set on the first
 * item added. See CartServiceImpl#addItem for what happens when a second
 * item from a DIFFERENT restaurant is added.
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
