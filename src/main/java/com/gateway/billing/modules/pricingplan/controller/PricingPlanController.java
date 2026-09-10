package com.gateway.billing.modules.pricingplan.controller;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.response.ApiResponse;
import com.gateway.billing.modules.pricingplan.dto.*;
import com.gateway.billing.modules.pricingplan.model.PricingPlanStatus;
import com.gateway.billing.modules.pricingplan.model.PricingPlanType;
import com.gateway.billing.modules.pricingplan.service.PricingPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pricing-plans")
@Tag(name = "PricingPlan", description = "Pricing plan management operations")
public class PricingPlanController {

    private final PricingPlanService pricingPlanService;

    public PricingPlanController(PricingPlanService pricingPlanService) {
        this.pricingPlanService = pricingPlanService;
    }

    @PostMapping
    @Operation(summary = "Create a new pricing plan")
    public ResponseEntity<ApiResponse<PricingPlanResponse>> create(@Valid @RequestBody PricingPlanCreateRequest request) {
        PricingPlanResponse response = pricingPlanService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Pricing plan created successfully"));
    }

    @GetMapping
    @Operation(summary = "List pricing plans with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<PricingPlanResponse>>> list(
            @RequestParam(required = false) PricingPlanType type,
            @RequestParam(required = false) PricingPlanStatus status,
            @ModelAttribute CursorParams params) {
        CursorPage<PricingPlanResponse> response = pricingPlanService.list(type, status, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pricing plan by ID")
    public ResponseEntity<ApiResponse<PricingPlanResponse>> getById(@PathVariable UUID id) {
        PricingPlanResponse response = pricingPlanService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pricing plan")
    public ResponseEntity<ApiResponse<PricingPlanResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody PricingPlanUpdateRequest request) {
        PricingPlanResponse response = pricingPlanService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Pricing plan updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change pricing plan status")
    public ResponseEntity<ApiResponse<PricingPlanResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PricingPlanStatusRequest request) {
        PricingPlanResponse response = pricingPlanService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Pricing plan status updated"));
    }
}
