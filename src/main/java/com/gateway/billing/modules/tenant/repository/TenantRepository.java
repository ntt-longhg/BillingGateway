package com.gateway.billing.modules.tenant.repository;

import com.gateway.billing.modules.tenant.model.Tenant;
import com.gateway.billing.modules.tenant.model.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    boolean existsByClientId(String clientId);

    boolean existsByClientIdAndIdNot(String clientId, UUID id);

    Optional<Tenant> findByClientId(String clientId);

    @Query("SELECT t FROM Tenant t WHERE t.deletedAt IS NULL AND (:cursor IS NULL OR t.id > :cursor) AND (:status IS NULL OR t.status = :status) AND (:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.clientId) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY t.id ASC")
    List<Tenant> findWithCursor(@Param("cursor") UUID cursor,
                                @Param("status") TenantStatus status,
                                @Param("keyword") String keyword,
                                org.springframework.data.domain.Pageable pageable);
}
