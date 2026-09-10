package com.gateway.billing.modules.servicecatalog.repository;

import com.gateway.billing.modules.servicecatalog.model.PriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceTierRepository extends JpaRepository<PriceTier, UUID> {

    List<PriceTier> findByServicePriceIdOrderByTierAsc(UUID servicePriceId);
}
