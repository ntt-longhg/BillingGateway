package com.gateway.walletcentral.modules.wallet.dto;

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
@Schema(description = "Request to change wallet status")
public class WalletStatusRequest {

    @NotNull(message = "Status must not be null")
    @Schema(description = "New wallet status", example = "ACTIVE")
    private com.gateway.walletcentral.modules.wallet.model.WalletStatus status;
}
