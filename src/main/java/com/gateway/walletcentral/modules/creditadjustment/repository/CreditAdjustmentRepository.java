package com.gateway.walletcentral.modules.creditadjustment.repository;

import com.gateway.walletcentral.modules.creditadjustment.model.CreditAdjustment;
import com.gateway.walletcentral.modules.creditadjustment.model.CreditAdjustmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditAdjustmentRepository extends JpaRepository<CreditAdjustment, UUID> {

    @Query("SELECT ca FROM CreditAdjustment ca JOIN FETCH ca.wallet WHERE (:cursor IS NULL OR ca.id > :cursor) AND (:walletId IS NULL OR ca.wallet.id = :walletId) AND (:type IS NULL OR ca.type = :type) ORDER BY ca.id ASC")
    List<CreditAdjustment> findWithCursor(@Param("cursor") UUID cursor,
                                          @Param("walletId") UUID walletId,
                                          @Param("type") CreditAdjustmentType type,
                                          org.springframework.data.domain.Pageable pageable);

    List<CreditAdjustment> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    boolean existsByWalletIdAndReferenceId(UUID walletId, String referenceId);
}
