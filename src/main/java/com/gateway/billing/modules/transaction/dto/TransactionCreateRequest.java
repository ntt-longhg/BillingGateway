package com.gateway.billing.modules.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Request to create a transaction")
public class TransactionCreateRequest {

    @NotNull(message = "Wallet ID must not be null")
    @Schema(description = "Wallet ID")
    private UUID walletId;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;

    @NotNull(message = "Type must not be null")
    @Schema(description = "Transaction type", example = "CHARGE")
    private com.gateway.billing.modules.transaction.model.TransactionType type;

    @NotBlank(message = "Reference from must not be blank")
    @Schema(description = "Reference source system", example = "API")
    private String referenceFrom;

    @NotBlank(message = "Reference ID must not be blank")
    @Schema(description = "Reference ID from source system", example = "TXN-12345")
    private String referenceId;

    @Schema(description = "Transaction description")
    private String description;
}
