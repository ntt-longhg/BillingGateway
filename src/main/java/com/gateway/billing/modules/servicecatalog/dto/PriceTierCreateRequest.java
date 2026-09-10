package com.gateway.billing.modules.servicecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a price tier")
public class PriceTierCreateRequest {

    @NotBlank(message = "Tier name must not be blank")
    @Size(min = 1, max = 100, message = "Tier name must be between 1 and 100 characters")
    @Schema(description = "Tier name", example = "TIER_1")
    private String tier;

    @NotNull(message = "Basic fee must not be null")
    @PositiveOrZero(message = "Basic fee must be zero or positive")
    @Schema(description = "Basic fee for this tier", example = "10.00")
    private BigDecimal basicFee;

    @NotNull(message = "Extended size must not be null")
    @Positive(message = "Extended size must be positive")
    @Schema(description = "Extended size in units", example = "100")
    private Integer extendedSize;

    @NotNull(message = "Extended fee must not be null")
    @PositiveOrZero(message = "Extended fee must be zero or positive")
    @Schema(description = "Extended fee for this tier", example = "5.00")
    private BigDecimal extendedFee;
}
