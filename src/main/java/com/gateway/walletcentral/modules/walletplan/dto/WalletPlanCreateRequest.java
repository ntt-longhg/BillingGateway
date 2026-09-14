package com.gateway.walletcentral.modules.walletplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a wallet plan")
public class WalletPlanCreateRequest {

    @NotNull(message = "Tenant ID must not be null")
    @Schema(description = "Tenant ID")
    private UUID tenantId;

    @NotNull(message = "Pricing plan ID must not be null")
    @Schema(description = "Pricing plan ID")
    private UUID pricingPlanId;

    @NotBlank(message = "Created by must not be blank")
    @Schema(description = "Creator identifier", example = "admin")
    private String createdBy;
}
