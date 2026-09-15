package com.gateway.walletcentral.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User info with role and permissions")
public class UserInfoResponse {

    @Schema(description = "User email")
    private String email;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Display name")
    private String displayName;

    @Schema(description = "Assigned role ID")
    private UUID roleId;

    @Schema(description = "Assigned role name")
    private String roleName;

    @Schema(description = "Effective permissions (role + overrides)")
    private Set<String> permissions;

    @Schema(description = "User-specific permission overrides")
    private List<UserPermissionOverrideResponse> overrides;
}
