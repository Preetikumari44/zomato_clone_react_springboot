package com.novabyte.zomatoclone.delivery.repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.novabyte.zomatoclone.common.enums.DeliveryStatus;
import com.novabyte.zomatoclone.delivery.entity.DeliveryAssignment;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    Optional<DeliveryAssignment> findByOrderId(Long orderId);

    /** The self-service pool: READY_FOR_PICKUP orders nobody has claimed yet. */
    @Query("SELECT da FROM DeliveryAssignment da WHERE da.status = 'UNASSIGNED' " +
           "AND da.order.status = com.novabyte.zomatoclone.common.enums.OrderStatus.READY_FOR_PICKUP")
    Page<DeliveryAssignment> findAvailableForPickup(Pageable pageable);

    Page<DeliveryAssignment> findByDeliveryPartnerIdAndStatusIn(Long deliveryPartnerId, java.util.List<DeliveryStatus> statuses, Pageable pageable);

    Page<DeliveryAssignment> findByDeliveryPartnerIdAndStatus(Long deliveryPartnerId, DeliveryStatus status, Pageable pageable);
}
