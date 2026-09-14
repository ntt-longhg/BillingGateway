package com.gateway.walletcentral.modules.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a tenant")
public class TenantUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Tenant name", example = "Acme Corp")
    private String name;

    @Size(min = 2, max = 100, message = "Client ID must be between 2 and 100 characters")
    @Schema(description = "Unique client identifier", example = "acme-corp")
    private String clientId;

    @Size(min = 8, message = "Client secret must be at least 8 characters")
    @Schema(description = "Client secret for authentication")
    private String clientSecret;

    @Schema(description = "Comma-separated allowed domains", example = "acme.com,acme.co.th")
    private String allowedDomains;
}
