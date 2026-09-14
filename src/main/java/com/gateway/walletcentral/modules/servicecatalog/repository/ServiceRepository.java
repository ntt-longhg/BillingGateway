package com.gateway.walletcentral.modules.servicecatalog.repository;

import com.gateway.walletcentral.modules.servicecatalog.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    Optional<Service> findByCode(String code);

    @Query("SELECT s FROM Service s WHERE s.deletedAt IS NULL AND (:cursor IS NULL OR s.id > :cursor) AND (:keyword IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY s.id ASC")
    List<Service> findWithCursor(@Param("cursor") UUID cursor,
                                 @Param("keyword") String keyword,
                                 org.springframework.data.domain.Pageable pageable);
}
