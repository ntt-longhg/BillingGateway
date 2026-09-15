package com.gateway.walletcentral.modules.auth.repository;

import com.gateway.walletcentral.modules.auth.model.AdminUserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminUserPermissionRepository extends JpaRepository<AdminUserPermission, String> {

    List<AdminUserPermission> findByAdminUserId(String adminUserId);

    void deleteByAdminUserId(String adminUserId);

    void deleteByAdminUserIdAndPermissionId(String adminUserId, UUID permissionId);

    boolean existsByAdminUserIdAndPermissionCode(String adminUserId, String permissionCode);
}
