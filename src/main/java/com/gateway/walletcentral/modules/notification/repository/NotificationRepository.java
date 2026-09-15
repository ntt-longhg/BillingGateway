package com.gateway.walletcentral.modules.notification.repository;

import com.gateway.walletcentral.modules.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.tenant WHERE (:cursor IS NULL OR n.id > :cursor) AND (:tenantId IS NULL OR n.tenant.id = :tenantId) AND (:isRead IS NULL OR n.isRead = :isRead) ORDER BY n.createdAt DESC")
    List<Notification> findWithCursor(@Param("cursor") UUID cursor,
                                      @Param("tenantId") UUID tenantId,
                                      @Param("isRead") Boolean isRead,
                                      org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenant.id = :tenantId AND n.isRead = false")
    long countUnreadByTenantId(@Param("tenantId") UUID tenantId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.tenant.id = :tenantId AND n.isRead = false")
    int markAllAsReadByTenantId(@Param("tenantId") UUID tenantId);
}
