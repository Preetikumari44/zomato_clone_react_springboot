package com.novabyte.zomatoclone.menu.repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.novabyte.zomatoclone.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId " +
           "AND (:categoryId IS NULL OR m.category.id = :categoryId) " +
           "AND LOWER(COALESCE(m.name, '')) LIKE :keywordPattern " +
           "AND (:veg IS NULL OR m.veg = :veg)")
    Page<MenuItem> search(@Param("restaurantId") Long restaurantId,
                           @Param("categoryId") Long categoryId,
                           @Param("keywordPattern") String keywordPattern,
                           @Param("veg") Boolean veg,
                           Pageable pageable);
}
