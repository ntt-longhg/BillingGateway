package com.gateway.billing.modules.usagelog.repository;

import com.gateway.billing.modules.usagelog.model.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog, UUID> {

    @Query("SELECT ul FROM UsageLog ul JOIN FETCH ul.tenant JOIN FETCH ul.service WHERE (:cursor IS NULL OR ul.id > :cursor) AND (:tenantId IS NULL OR ul.tenant.id = :tenantId) AND (:serviceId IS NULL OR ul.service.id = :serviceId) ORDER BY ul.id ASC")
    List<UsageLog> findWithCursor(@Param("cursor") UUID cursor,
                                  @Param("tenantId") UUID tenantId,
                                  @Param("serviceId") UUID serviceId,
                                  org.springframework.data.domain.Pageable pageable);
}
