package com.gateway.billing.modules.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingWebhookRequest {

    @NotBlank(message = "Service code is required")
    @Schema(description = "Service code matching service_catalogs.code", example = "SMS")
    private String serviceCode;

    @NotNull(message = "Usage units is required")
    @Min(value = 1, message = "Usage units must be at least 1")
    @Schema(description = "Number of usage units", example = "100")
    private Integer usageUnits;

    @NotBlank(message = "Reference ID is required")
    @Schema(description = "Unique reference ID from calling service", example = "SMS-20260914-001")
    private String referenceId;

    @Schema(description = "Optional description", example = "SMS verification codes batch")
    private String description;

    @NotBlank(message = "Webhook URL is required")
    @Schema(description = "URL to receive billing result callback", example = "https://service.example.com/webhook/billing")
    private String webhookUrl;

    @Schema(description = "Optional webhook authentication header value", example = "Bearer abc123")
    private String webhookAuth;

    @Schema(description = "Optional metadata to pass through")
    private Map<String, Object> metadata;
}
