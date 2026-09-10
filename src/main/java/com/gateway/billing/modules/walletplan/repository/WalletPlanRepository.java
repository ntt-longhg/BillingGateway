package com.gateway.billing.modules.walletplan.repository;

import com.gateway.billing.modules.walletplan.model.WalletPlan;
import com.gateway.billing.modules.walletplan.model.WalletPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletPlanRepository extends JpaRepository<WalletPlan, UUID> {

    @Query("SELECT wp FROM WalletPlan wp JOIN FETCH wp.pricingPlan JOIN FETCH wp.tenant WHERE wp.deletedAt IS NULL AND (:cursor IS NULL OR wp.id > :cursor) AND (:tenantId IS NULL OR wp.tenant.id = :tenantId) AND (:status IS NULL OR wp.status = :status) ORDER BY wp.id ASC")
    List<WalletPlan> findWithCursor(@Param("cursor") UUID cursor,
                                    @Param("tenantId") UUID tenantId,
                                    @Param("status") WalletPlanStatus status,
                                    org.springframework.data.domain.Pageable pageable);

    List<WalletPlan> findByStatusOrderByCreatedAtAsc(WalletPlanStatus status);
}
