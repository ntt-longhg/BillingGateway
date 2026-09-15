package com.gateway.walletcentral.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to grant/revoke a user-specific permission override")
public class UserPermissionRequest {

    @NotNull(message = "Permission ID must not be null")
    @Schema(description = "Permission ID to grant or revoke")
    private UUID permissionId;
}
