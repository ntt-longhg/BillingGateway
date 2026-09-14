package com.gateway.walletcentral.modules.systemconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigUpdateRequest {

    @NotBlank(message = "Config key is required")
    @Schema(description = "Configuration key", example = "smtp.host")
    private String key;

    @NotBlank(message = "Config value is required")
    @Schema(description = "Configuration value", example = "smtp.gmail.com")
    private String value;
}
