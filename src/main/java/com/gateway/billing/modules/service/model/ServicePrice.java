package com.gateway.billing.modules.service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.ColumnDefault;
import java.util.UUID;

@Entity
@Table(name = "service_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePrice {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "initial_size", nullable = false)
    private Integer initialSize;

    @Column(name = "initial_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal initialFee = BigDecimal.ZERO;

    @Column(name = "subsequent_size", nullable = false)
    private Integer subsequentSize;

    @Column(name = "subsequent_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal subsequentFee = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = true)
    @ColumnDefault("1")
    private Boolean isActive = true;

    @Column(name = "effective_date", nullable = false)
    private OffsetDateTime effectiveDate;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "deleted_at", nullable = true)
    private OffsetDateTime deletedAt = null;
}
