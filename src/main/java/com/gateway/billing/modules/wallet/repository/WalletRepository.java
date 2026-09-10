package com.gateway.billing.modules.wallet.repository;

import com.gateway.billing.modules.wallet.model.Wallet;
import com.gateway.billing.modules.wallet.model.WalletStatus;
import com.gateway.billing.modules.wallet.model.WalletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByTenantId(UUID tenantId);

    boolean existsByTenantId(UUID tenantId);

    @Query("SELECT w FROM Wallet w JOIN FETCH w.tenant WHERE w.deletedAt IS NULL AND (:cursor IS NULL OR w.id > :cursor) AND (:tenantId IS NULL OR w.tenant.id = :tenantId) AND (:type IS NULL OR w.type = :type) AND (:status IS NULL OR w.status = :status) ORDER BY w.id ASC")
    List<Wallet> findWithCursor(@Param("cursor") UUID cursor,
                                @Param("tenantId") UUID tenantId,
                                @Param("type") WalletType type,
                                @Param("status") WalletStatus status,
                                org.springframework.data.domain.Pageable pageable);
}
