package com.gateway.walletcentral.modules.auth.repository;

import com.gateway.walletcentral.modules.auth.model.AdminUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRoleRepository extends JpaRepository<AdminUserRole, String> {

    Optional<AdminUserRole> findByAdminUserId(String adminUserId);

    void deleteByAdminUserId(String adminUserId);

    boolean existsByAdminUserIdAndRoleName(String adminUserId, String roleName);
}
