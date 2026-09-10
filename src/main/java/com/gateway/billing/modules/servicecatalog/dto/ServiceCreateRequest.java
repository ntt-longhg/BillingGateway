package com.gateway.billing.modules.servicecatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a service")
public class ServiceCreateRequest {

    @NotBlank(message = "Code must not be blank")
    @Size(min = 2, max = 100, message = "Code must be between 2 and 100 characters")
    @Schema(description = "Unique service code", example = "SMS")
    private String code;

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "Service name", example = "SMS Service")
    private String name;

    @Schema(description = "Service description", example = "SMS messaging service")
    private String description;
}
