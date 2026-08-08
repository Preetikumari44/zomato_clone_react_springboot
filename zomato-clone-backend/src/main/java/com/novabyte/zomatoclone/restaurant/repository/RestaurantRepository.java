package com.novabyte.zomatoclone.restaurant.repository;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.novabyte.zomatoclone.common.enums.RestaurantStatus;
import com.novabyte.zomatoclone.restaurant.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Public search — deliberately hardcodes status = APPROVED so an
     * unapproved or suspended restaurant can never surface to customers,
     * no matter what filters are passed.
     */
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' " +
           "AND (:keyword IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:city IS NULL OR LOWER(r.city) = LOWER(:city)) " +
           "AND (:cuisineType IS NULL OR LOWER(r.cuisineType) = LOWER(:cuisineType))")
    Page<Restaurant> searchApproved(@Param("keyword") String keyword,
                                     @Param("city") String city,
                                     @Param("cuisineType") String cuisineType,
                                     Pageable pageable);

    Page<Restaurant> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

    long countByStatus(RestaurantStatus status);
}
