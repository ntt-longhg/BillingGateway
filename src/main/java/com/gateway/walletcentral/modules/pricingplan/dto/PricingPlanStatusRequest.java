package com.gateway.walletcentral.modules.pricingplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to change pricing plan status")
public class PricingPlanStatusRequest {

    @NotNull(message = "Status must not be null")
    @Schema(description = "New status", example = "ACTIVE")
    private com.gateway.walletcentral.modules.pricingplan.model.PricingPlanStatus status;
}
