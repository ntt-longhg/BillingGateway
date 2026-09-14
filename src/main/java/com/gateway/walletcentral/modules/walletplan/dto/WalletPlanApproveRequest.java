package com.gateway.walletcentral.modules.walletplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to approve a wallet plan")
public class WalletPlanApproveRequest {

    @NotBlank(message = "Approved by must not be blank")
    @Schema(description = "Approver identifier", example = "admin")
    private String approvedBy;
}
