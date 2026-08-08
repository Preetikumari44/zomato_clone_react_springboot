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
           "AND LOWER(COALESCE(r.name, '')) LIKE :keywordPattern " +
           "AND LOWER(COALESCE(r.city, '')) LIKE :cityPattern " +
           "AND LOWER(COALESCE(r.cuisineType, '')) LIKE :cuisinePattern")
    Page<Restaurant> searchApproved(@Param("keywordPattern") String keywordPattern,
                                     @Param("cityPattern") String cityPattern,
                                     @Param("cuisinePattern") String cuisinePattern,
                                     Pageable pageable);

    Page<Restaurant> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

    long countByStatus(RestaurantStatus status);
}
