package com.gateway.billing.modules.creditadjustment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request to create a credit adjustment")
public class CreditAdjustmentCreateRequest {

    @NotNull(message = "Wallet ID must not be null")
    @Schema(description = "Wallet ID")
    private UUID walletId;

    @NotNull(message = "Type must not be null")
    @Schema(description = "Adjustment type", example = "INCREASE")
    private com.gateway.billing.modules.creditadjustment.model.CreditAdjustmentType type;

    @NotNull(message = "Adjustment amount must not be null")
    @Schema(description = "Adjustment amount", example = "5000.00")
    private BigDecimal adjustmentAmount;

    @NotBlank(message = "Reason must not be blank")
    @Schema(description = "Adjustment reason", example = "Credit limit increase request")
    private String reason;

    @NotBlank(message = "Reference from must not be blank")
    @Schema(description = "Reference source system", example = "API")
    private String referenceFrom;

    @NotBlank(message = "Reference ID must not be blank")
    @Schema(description = "Reference ID from source system", example = "CA-12345")
    private String referenceId;

    @NotBlank(message = "Created by must not be blank")
    @Schema(description = "Creator identifier", example = "admin")
    private String createdBy;
}
