package com.gateway.billing.modules.pricingplan.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "pricing_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingPlan {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description = null;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "bonus_type",nullable = false, length = 20)
    private BonusType bonusType = BonusType.PERCENTAGE;

    @Column(name = "bonus_value", nullable = true, precision = 15, scale = 2)
    private BigDecimal bonusValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false, length = 20)
    private PricingPlanStatus status = PricingPlanStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "deleted_at", nullable = true)
    private OffsetDateTime deletedAt = null;
}
