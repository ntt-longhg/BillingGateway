package com.gateway.billing.modules.invoice.dto;

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
@Schema(description = "Request to mark invoice as paid")
public class InvoicePayRequest {

    @NotBlank(message = "Updated by must not be blank")
    @Schema(description = "Person marking as paid", example = "admin")
    private String updatedBy;
}
