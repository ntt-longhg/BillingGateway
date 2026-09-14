package com.gateway.walletcentral.modules.systemconfig.controller;

import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.systemconfig.dto.SystemConfigResponse;
import com.gateway.walletcentral.modules.systemconfig.dto.SystemConfigUpdateRequest;
import com.gateway.walletcentral.modules.systemconfig.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system-configs")
@Tag(name = "System Config", description = "System configuration management operations")
public class SystemConfigController {

    private final SystemConfigService configService;

    public SystemConfigController(SystemConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    @Operation(summary = "List all system configurations, optionally filtered by group")
    public ResponseEntity<ApiResponse<List<SystemConfigResponse>>> list(
            @RequestParam(required = false) String group) {
        List<SystemConfigResponse> configs = configService.getAll(group);
        return ResponseEntity.ok(ApiResponse.ok(configs));
    }

    @PutMapping
    @Operation(summary = "Bulk update system configurations")
    public ResponseEntity<ApiResponse<Map<String, String>>> update(
            @RequestBody Map<String, List<SystemConfigUpdateRequest>> body) {
        List<SystemConfigUpdateRequest> configs = body.get("configs");
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("Configs list is required");
        }
        configService.updateConfigs(configs);
        configService.invalidateCache();
        return ResponseEntity.ok(ApiResponse.ok(null, "Configurations updated successfully"));
    }
}
