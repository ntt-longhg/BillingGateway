package com.gateway.walletcentral.modules.pricingplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a pricing plan")
public class PricingPlanUpdateRequest {

    @Schema(description = "Plan name", example = "Topup 100")
    private String name;

    @Schema(description = "Plan description")
    private String description;

    @PositiveOrZero(message = "Price must be zero or positive")
    @Schema(description = "Plan price", example = "100.00")
    private BigDecimal price;

    @Schema(description = "Bonus type", example = "PERCENTAGE")
    private com.gateway.walletcentral.modules.pricingplan.model.BonusType bonusType;

    @Schema(description = "Bonus value")
    private BigDecimal bonusValue;

    @Schema(description = "Credit limit action", example = "NONE")
    private com.gateway.walletcentral.modules.pricingplan.model.CreditLimitAction creditLimitAction;

    @Schema(description = "Credit limit value", example = "0.00")
    private BigDecimal creditLimitValue;
}
