package com.gateway.billing.modules.service.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTier {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_price_id", nullable = false)
    private ServicePrice servicePrice;

    @Column(nullable = false, length = 100)
    private String tier;

    @Column(name = "basic_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal basicFee = BigDecimal.ZERO;

    @Column(name = "extended_size", nullable = false)
    private Integer extendedSize;

    @Column(name = "extended_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal extendedFee = BigDecimal.ZERO;
}
