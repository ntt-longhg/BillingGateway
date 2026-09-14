package com.gateway.walletcentral.modules.servicecatalog.dto;

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
@Schema(description = "Request to update a service")
public class ServiceUpdateRequest {

    @Size(min = 2, max = 100, message = "Code must be between 2 and 100 characters")
    @Schema(description = "Unique service code", example = "SMS")
    private String code;

    @Schema(description = "Service name", example = "SMS Service")
    private String name;

    @Schema(description = "Service description")
    private String description;
}
