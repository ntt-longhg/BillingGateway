package com.gateway.walletcentral.modules.pricingplan.repository;

import com.gateway.walletcentral.modules.pricingplan.model.PricingPlan;
import com.gateway.walletcentral.modules.pricingplan.model.PricingPlanStatus;
import com.gateway.walletcentral.modules.pricingplan.model.PricingPlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, UUID> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    Optional<PricingPlan> findByCode(String code);

    @Query("SELECT pp FROM PricingPlan pp WHERE pp.deletedAt IS NULL AND (:cursor IS NULL OR pp.id > :cursor) AND (:type IS NULL OR pp.type = :type) AND (:status IS NULL OR pp.status = :status) ORDER BY pp.id ASC")
    List<PricingPlan> findWithCursor(@Param("cursor") UUID cursor,
                                     @Param("type") PricingPlanType type,
                                     @Param("status") PricingPlanStatus status,
                                     org.springframework.data.domain.Pageable pageable);
}
