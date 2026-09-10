package com.gateway.billing.modules.usagelog.controller;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.response.ApiResponse;
import com.gateway.billing.modules.usagelog.dto.*;
import com.gateway.billing.modules.usagelog.service.UsageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usage-logs")
@Tag(name = "UsageLog", description = "Usage log recording and querying")
public class UsageLogController {

    private final UsageLogService usageLogService;

    public UsageLogController(UsageLogService usageLogService) {
        this.usageLogService = usageLogService;
    }

    @PostMapping
    @Operation(summary = "Record a usage log")
    public ResponseEntity<ApiResponse<UsageLogResponse>> create(@Valid @RequestBody UsageLogCreateRequest request) {
        UsageLogResponse response = usageLogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Usage log recorded successfully"));
    }

    @GetMapping
    @Operation(summary = "List usage logs with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<UsageLogResponse>>> list(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) UUID serviceId,
            @ModelAttribute CursorParams params) {
        CursorPage<UsageLogResponse> response = usageLogService.list(tenantId, serviceId, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get usage log by ID")
    public ResponseEntity<ApiResponse<UsageLogResponse>> getById(@PathVariable UUID id) {
        UsageLogResponse response = usageLogService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
