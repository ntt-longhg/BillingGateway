package com.gateway.walletcentral.modules.usagelog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to record a usage log")
public class UsageLogCreateRequest {

    @NotNull(message = "Tenant ID must not be null")
    @Schema(description = "Tenant ID")
    private UUID tenantId;

    @NotNull(message = "Service ID must not be null")
    @Schema(description = "Service ID")
    private UUID serviceId;

    @NotNull(message = "Total usage must not be null")
    @Positive(message = "Total usage must be positive")
    @Schema(description = "Total usage in units", example = "500")
    private Integer totalUsage;

    @NotBlank(message = "Reference from must not be blank")
    @Schema(description = "Reference source system", example = "API")
    private String referenceFrom;

    @NotBlank(message = "Reference ID must not be blank")
    @Schema(description = "Reference ID from source system", example = "UL-12345")
    private String referenceId;
}
