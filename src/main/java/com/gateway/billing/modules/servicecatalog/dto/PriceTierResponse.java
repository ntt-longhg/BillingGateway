package com.gateway.billing.modules.servicecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Price tier response")
public class PriceTierResponse {

    @Schema(description = "Price tier ID")
    private UUID id;

    @Schema(description = "Service price ID")
    private UUID servicePriceId;

    @Schema(description = "Tier name", example = "TIER_1")
    private String tier;

    @Schema(description = "Basic fee for this tier")
    private BigDecimal basicFee;

    @Schema(description = "Extended size in units")
    private Integer extendedSize;

    @Schema(description = "Extended fee for this tier")
    private BigDecimal extendedFee;
}
