package com.gateway.walletcentral.modules.auth.repository;

import com.gateway.walletcentral.modules.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    List<Permission> findByModule(String module);

    @Query("SELECT p FROM Permission p ORDER BY p.module ASC, p.code ASC")
    List<Permission> findAllOrdered();

    @Query("SELECT p FROM Permission p WHERE p.id IN :ids")
    List<Permission> findByIdIn(@Param("ids") List<UUID> ids);
}
