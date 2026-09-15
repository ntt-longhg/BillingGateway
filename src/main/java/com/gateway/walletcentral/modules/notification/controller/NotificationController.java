package com.gateway.walletcentral.modules.notification.controller;

import com.gateway.walletcentral.core.annotation.RequirePermission;
import com.gateway.walletcentral.core.cursor.CursorPage;
import com.gateway.walletcentral.core.cursor.CursorParams;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.notification.dto.NotificationResponse;
import com.gateway.walletcentral.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Notification management (admin only)")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @RequirePermission("NOTIFICATION_VIEW")
    @Operation(summary = "List notifications with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<NotificationResponse>>> list(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) Boolean isRead,
            @ModelAttribute CursorParams params) {
        UUID cursor = params.getCursor() != null ? UUID.fromString(params.getCursor()) : null;
        CursorPage<NotificationResponse> response = notificationService.list(tenantId, isRead, cursor, params.getSize());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/unread-count")
    @RequirePermission("NOTIFICATION_VIEW")
    @Operation(summary = "Get unread notification count for a tenant")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @RequestParam UUID tenantId) {
        long count = notificationService.getUnreadCount(tenantId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    @RequirePermission("NOTIFICATION_VIEW")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    @RequirePermission("NOTIFICATION_VIEW")
    @Operation(summary = "Mark all notifications as read for a tenant")
    public ResponseEntity<ApiResponse<Map<String, String>>> markAllAsRead(
            @RequestParam UUID tenantId) {
        notificationService.markAllAsRead(tenantId);
        return ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read"));
    }
}
