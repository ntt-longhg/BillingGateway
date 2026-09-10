package com.gateway.billing.modules.pricingplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request to create a pricing plan")
public class PricingPlanCreateRequest {

    @NotBlank(message = "Code must not be blank")
    @Schema(description = "Unique plan code", example = "TOPUP_100")
    private String code;

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "Plan name", example = "Topup 100")
    private String name;

    @Schema(description = "Plan description", example = "Top up balance with bonus")
    private String description;

    @NotNull(message = "Price must not be null")
    @PositiveOrZero(message = "Price must be zero or positive")
    @Schema(description = "Plan price", example = "100.00")
    private BigDecimal price;

    @NotNull(message = "Type must not be null")
    @Schema(description = "Plan type", example = "BALANCE_TOPUP")
    private com.gateway.billing.modules.pricingplan.model.PricingPlanType type;

    @NotNull(message = "Bonus type must not be null")
    @Schema(description = "Bonus type", example = "PERCENTAGE")
    private com.gateway.billing.modules.pricingplan.model.BonusType bonusType;

    @Schema(description = "Bonus value (percentage or fixed amount)")
    private BigDecimal bonusValue;

    @NotNull(message = "Credit limit action must not be null")
    @Schema(description = "Credit limit action", example = "NONE")
    private com.gateway.billing.modules.pricingplan.model.CreditLimitAction creditLimitAction;

    @NotNull(message = "Credit limit value must not be null")
    @PositiveOrZero(message = "Credit limit value must be zero or positive")
    @Schema(description = "Credit limit value", example = "0.00")
    private BigDecimal creditLimitValue;
}
