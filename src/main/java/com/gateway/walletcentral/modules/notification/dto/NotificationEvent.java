package com.gateway.walletcentral.modules.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Notification event for RabbitMQ processing")
public class NotificationEvent {

    @Schema(description = "Target tenant ID")
    private UUID tenantId;

    @Schema(description = "Notification type", example = "TRANSACTION")
    private String type;

    @Schema(description = "Notification title")
    private String title;

    @Schema(description = "Notification message")
    private String message;

    @Schema(description = "Related entity type")
    private String referenceType;

    @Schema(description = "Related entity ID")
    private String referenceId;

    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
