package com.novabyte.zomatoclone.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novabyte.zomatoclone.menu.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByRestaurantId(Long restaurantId);
    Optional<Category> findByIdAndRestaurantId(Long id, Long restaurantId);
}
