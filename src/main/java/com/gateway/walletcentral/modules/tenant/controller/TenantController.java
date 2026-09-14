package com.gateway.walletcentral.modules.tenant.controller;

import com.gateway.walletcentral.core.cursor.CursorPage;
import com.gateway.walletcentral.core.cursor.CursorParams;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.tenant.dto.*;
import com.gateway.walletcentral.modules.tenant.model.TenantStatus;
import com.gateway.walletcentral.modules.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenant", description = "Tenant management operations")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> create(@Valid @RequestBody TenantCreateRequest request) {
        TenantResponse response = tenantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Tenant created successfully"));
    }

    @GetMapping
    @Operation(summary = "List tenants with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<TenantResponse>>> list(
            @RequestParam(required = false) TenantStatus status,
            @RequestParam(required = false) String keyword,
            @ModelAttribute CursorParams params) {
        CursorPage<TenantResponse> response = tenantService.list(status, keyword, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    public ResponseEntity<ApiResponse<TenantResponse>> getById(@PathVariable UUID id) {
        TenantResponse response = tenantService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TenantUpdateRequest request) {
        TenantResponse response = tenantService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Tenant updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change tenant status")
    public ResponseEntity<ApiResponse<TenantResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody TenantStatusRequest request) {
        TenantResponse response = tenantService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Tenant status updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete tenant")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        tenantService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Tenant deleted successfully"));
    }
}
