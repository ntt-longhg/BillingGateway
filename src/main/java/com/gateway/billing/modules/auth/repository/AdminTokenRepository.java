package com.gateway.billing.modules.auth.repository;

import com.gateway.billing.modules.auth.model.AdminToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminTokenRepository extends JpaRepository<AdminToken, UUID> {

    Optional<AdminToken> findByToken(String token);

    boolean existsByTokenAndExpiresAtAfter(String token, OffsetDateTime now);

    int deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM AdminToken t WHERE t.expiresAt < :now")
    int deleteExpired(OffsetDateTime now);
}
