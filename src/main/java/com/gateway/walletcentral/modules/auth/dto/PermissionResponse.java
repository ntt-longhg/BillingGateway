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
@Schema(description = "Permission response")
public class PermissionResponse {

    @Schema(description = "Permission ID")
    private UUID id;

    @Schema(description = "Permission code", example = "TENANT_VIEW")
    private String code;

    @Schema(description = "Module this permission belongs to", example = "TENANT")
    private String module;

    @Schema(description = "Permission description")
    private String description;
}
