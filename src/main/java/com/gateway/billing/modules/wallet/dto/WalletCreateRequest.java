package com.gateway.billing.modules.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Schema(description = "Request to create a wallet")
public class WalletCreateRequest {

    @NotNull(message = "Tenant ID must not be null")
    @Schema(description = "Associated tenant ID")
    private UUID tenantId;

    @NotNull(message = "Wallet type must not be null")
    @Schema(description = "Wallet type", example = "PREPAID")
    private com.gateway.billing.modules.wallet.model.WalletType type;
}
