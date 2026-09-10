package com.gateway.billing.core.cursor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cursor-based pagination parameters")
public class CursorParams {

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must be at most 100")
    @Builder.Default
    @Schema(description = "Number of items per page", example = "20", defaultValue = "20")
    private int size = 20;

    @Schema(description = "Cursor for the next page (UUID of last item from previous page)", example = "0190d8e0-...", nullable = true)
    private String cursor;
}
