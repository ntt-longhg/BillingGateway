package com.gateway.walletcentral.modules.creditadjustment.controller;

import com.gateway.walletcentral.core.cursor.CursorPage;
import com.gateway.walletcentral.core.cursor.CursorParams;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.creditadjustment.dto.*;
import com.gateway.walletcentral.modules.creditadjustment.model.CreditAdjustmentType;
import com.gateway.walletcentral.modules.creditadjustment.service.CreditAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit-adjustments")
@Tag(name = "CreditAdjustment", description = "Credit limit adjustment operations")
public class CreditAdjustmentController {

    private final CreditAdjustmentService creditAdjustmentService;

    public CreditAdjustmentController(CreditAdjustmentService creditAdjustmentService) {
        this.creditAdjustmentService = creditAdjustmentService;
    }

    @PostMapping
    @Operation(summary = "Create a credit adjustment (increase, decrease, set)")
    public ResponseEntity<ApiResponse<CreditAdjustmentResponse>> create(@Valid @RequestBody CreditAdjustmentCreateRequest request) {
        CreditAdjustmentResponse response = creditAdjustmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Credit adjustment created successfully"));
    }

    @GetMapping
    @Operation(summary = "List credit adjustments with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<CreditAdjustmentResponse>>> list(
            @RequestParam(required = false) UUID walletId,
            @RequestParam(required = false) CreditAdjustmentType type,
            @ModelAttribute CursorParams params) {
        CursorPage<CreditAdjustmentResponse> response = creditAdjustmentService.list(walletId, type, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get credit adjustment by ID")
    public ResponseEntity<ApiResponse<CreditAdjustmentResponse>> getById(@PathVariable UUID id) {
        CreditAdjustmentResponse response = creditAdjustmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
