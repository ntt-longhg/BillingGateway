package com.gateway.billing.modules.wallet.controller;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.response.ApiResponse;
import com.gateway.billing.modules.wallet.dto.*;
import com.gateway.billing.modules.wallet.model.WalletStatus;
import com.gateway.billing.modules.wallet.model.WalletType;
import com.gateway.billing.modules.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet", description = "Wallet management operations")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    @Operation(summary = "Create a new wallet for a tenant")
    public ResponseEntity<ApiResponse<WalletResponse>> create(@Valid @RequestBody WalletCreateRequest request) {
        WalletResponse response = walletService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Wallet created successfully"));
    }

    @GetMapping
    @Operation(summary = "List wallets with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<WalletResponse>>> list(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) WalletType type,
            @RequestParam(required = false) WalletStatus status,
            @ModelAttribute CursorParams params) {
        CursorPage<WalletResponse> response = walletService.list(tenantId, type, status, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wallet by ID")
    public ResponseEntity<ApiResponse<WalletResponse>> getById(@PathVariable UUID id) {
        WalletResponse response = walletService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get wallet by tenant ID")
    public ResponseEntity<ApiResponse<WalletResponse>> getByTenantId(@PathVariable UUID tenantId) {
        WalletResponse response = walletService.getByTenantId(tenantId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change wallet status")
    public ResponseEntity<ApiResponse<WalletResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody WalletStatusRequest request) {
        WalletResponse response = walletService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Wallet status updated"));
    }
}
