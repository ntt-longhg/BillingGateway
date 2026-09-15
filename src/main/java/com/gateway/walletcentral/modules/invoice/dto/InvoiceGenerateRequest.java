package com.gateway.walletcentral.modules.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to generate invoice from usage logs")
public class InvoiceGenerateRequest {

    @NotNull(message = "Tenant ID must not be null")
    @Schema(description = "Tenant ID to generate invoice for")
    private UUID tenantId;

    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Billing period must be in yyyy-MM format")
    @Schema(description = "Billing period (optional, defaults to current month)", example = "2026-09")
    private String billingPeriod;

    @NotBlank(message = "Updated by must not be blank")
    @Schema(description = "Creator identifier", example = "admin")
    private String updatedBy;

    /**
     * Get billing period, defaulting to current month if not specified.
     */
    public String getEffectiveBillingPeriod() {
        if (billingPeriod == null || billingPeriod.isBlank()) {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return billingPeriod;
    }
}
