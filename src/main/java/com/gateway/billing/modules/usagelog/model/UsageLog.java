package com.gateway.billing.modules.usagelog.model;
import com.gateway.billing.modules.tenant.model.Tenant;
import com.gateway.billing.modules.service.model.Service;
import com.gateway.billing.modules.wallet.model.WalletType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "usage_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private WalletType walletTypeSnapshot = WalletType.PREPAID;

    @Column(name = "total_usage", nullable = false)
    private Integer totalUsage;

    @Column(name = "total_charged", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCharged = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fee_breakdown", columnDefinition = "longtext")
    private FeeBreakdownStructure feeBreakdown;

    @Column(name = "reference_from", nullable = false, length = 100)
    private String referenceFrom;

    @Column(name = "reference_id", nullable = false, length = 100)
    private String referenceId;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
