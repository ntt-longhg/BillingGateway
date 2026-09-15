package com.gateway.walletcentral.modules.auth.repository;

import com.gateway.walletcentral.modules.auth.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

    Optional<AdminUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
