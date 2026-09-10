package com.gateway.billing.modules.servicecatalog.repository;

import com.gateway.billing.modules.servicecatalog.model.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, UUID> {

    @Query("SELECT sp FROM ServicePrice sp WHERE sp.deletedAt IS NULL AND (:cursor IS NULL OR sp.id > :cursor) AND sp.service.id = :serviceId AND (:activeOnly IS NULL OR sp.isActive = :activeOnly) ORDER BY sp.id ASC")
    List<ServicePrice> findByServiceId(@Param("cursor") UUID cursor,
                                       @Param("serviceId") UUID serviceId,
                                       @Param("activeOnly") Boolean activeOnly,
                                       org.springframework.data.domain.Pageable pageable);

    List<ServicePrice> findByServiceIdAndIsActiveTrue(UUID serviceId);
}
