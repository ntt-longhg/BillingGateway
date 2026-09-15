package com.gateway.walletcentral.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request to create a role")
public class RoleCreateRequest {

    @NotBlank(message = "Role name must not be blank")
    @Size(min = 2, max = 50, message = "Role name must be 2-50 characters")
    @Schema(description = "Role name", example = "CUSTOM_ROLE")
    private String name;

    @Schema(description = "Role description")
    private String description;

    @Schema(description = "Permission IDs to assign to this role")
    private Set<UUID> permissionIds;
}
