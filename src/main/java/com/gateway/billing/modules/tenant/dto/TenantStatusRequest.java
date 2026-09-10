package com.gateway.billing.modules.tenant.dto;

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
@Schema(description = "Request to change tenant status")
public class TenantStatusRequest {

    @NotNull(message = "Status must not be null")
    @Schema(description = "New tenant status", example = "ACTIVE")
    private com.gateway.billing.modules.tenant.model.TenantStatus status;
}
