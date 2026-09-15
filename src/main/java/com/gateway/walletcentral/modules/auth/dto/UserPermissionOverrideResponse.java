package com.gateway.walletcentral.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User-specific permission override detail")
public class UserPermissionOverrideResponse {

    @Schema(description = "Permission ID")
    private UUID permissionId;

    @Schema(description = "Permission code")
    private String permissionCode;

    @Schema(description = "Whether this permission is granted (true) or revoked (false)")
    private Boolean isGranted;
}
