package com.gateway.walletcentral.modules.servicecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Request to update a price tier")
public class PriceTierUpdateRequest {

    @PositiveOrZero(message = "Basic fee must be zero or positive")
    @Schema(description = "Basic fee for this tier", example = "10.00")
    private BigDecimal basicFee;

    @Positive(message = "Extended size must be positive")
    @Schema(description = "Extended size in units", example = "100")
    private Integer extendedSize;

    @PositiveOrZero(message = "Extended fee must be zero or positive")
    @Schema(description = "Extended fee for this tier", example = "5.00")
    private BigDecimal extendedFee;
}
