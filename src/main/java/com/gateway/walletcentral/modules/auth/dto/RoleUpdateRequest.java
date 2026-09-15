package com.gateway.walletcentral.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a role")
public class RoleUpdateRequest {

    @Size(min = 2, max = 50, message = "Role name must be 2-50 characters")
    @Schema(description = "Role name")
    private String name;

    @Schema(description = "Role description")
    private String description;

    @Schema(description = "Permission IDs to assign (replaces all existing)")
    private Set<UUID> permissionIds;
}
