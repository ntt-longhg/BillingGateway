package com.gateway.billing.modules.servicecatalog.controller;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.response.ApiResponse;
import com.gateway.billing.modules.servicecatalog.dto.*;
import com.gateway.billing.modules.servicecatalog.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Service", description = "Service, ServicePrice, and PriceTier management")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ========== Service endpoints ==========

    @PostMapping
    @Operation(summary = "Create a new service")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody ServiceCreateRequest request) {
        ServiceResponse response = serviceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Service created successfully"));
    }

    @GetMapping
    @Operation(summary = "List services with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<ServiceResponse>>> listServices(
            @RequestParam(required = false) String keyword,
            @ModelAttribute CursorParams params) {
        CursorPage<ServiceResponse> response = serviceService.listServices(keyword, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable UUID id) {
        ServiceResponse response = serviceService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable UUID id,
            @Valid @RequestBody ServiceUpdateRequest request) {
        ServiceResponse response = serviceService.updateService(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Service updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete service")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable UUID id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Service deleted successfully"));
    }

    // ========== ServicePrice endpoints ==========

    @PostMapping("/{serviceId}/prices")
    @Operation(summary = "Create a service price")
    public ResponseEntity<ApiResponse<ServicePriceResponse>> createPrice(
            @PathVariable UUID serviceId,
            @Valid @RequestBody ServicePriceCreateRequest request) {
        ServicePriceResponse response = serviceService.createPrice(serviceId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Service price created successfully"));
    }

    @GetMapping("/{serviceId}/prices")
    @Operation(summary = "List service prices with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<ServicePriceResponse>>> listPrices(
            @PathVariable UUID serviceId,
            @RequestParam(required = false) Boolean activeOnly,
            @ModelAttribute CursorParams params) {
        CursorPage<ServicePriceResponse> response = serviceService.listPrices(serviceId, activeOnly, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/prices/{id}")
    @Operation(summary = "Get service price by ID")
    public ResponseEntity<ApiResponse<ServicePriceResponse>> getPriceById(@PathVariable UUID id) {
        ServicePriceResponse response = serviceService.getPriceById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/prices/{id}")
    @Operation(summary = "Update service price")
    public ResponseEntity<ApiResponse<ServicePriceResponse>> updatePrice(
            @PathVariable UUID id,
            @Valid @RequestBody ServicePriceUpdateRequest request) {
        ServicePriceResponse response = serviceService.updatePrice(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Service price updated successfully"));
    }

    @PatchMapping("/prices/{id}/activate")
    @Operation(summary = "Toggle service price active status")
    public ResponseEntity<ApiResponse<ServicePriceResponse>> togglePriceActive(@PathVariable UUID id) {
        ServicePriceResponse response = serviceService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Service price status toggled"));
    }

    // ========== PriceTier endpoints ==========

    @PostMapping("/prices/{priceId}/tiers")
    @Operation(summary = "Create a price tier")
    public ResponseEntity<ApiResponse<PriceTierResponse>> createTier(
            @PathVariable UUID priceId,
            @Valid @RequestBody PriceTierCreateRequest request) {
        PriceTierResponse response = serviceService.createTier(priceId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Price tier created successfully"));
    }

    @GetMapping("/prices/{priceId}/tiers")
    @Operation(summary = "List price tiers")
    public ResponseEntity<ApiResponse<List<PriceTierResponse>>> listTiers(@PathVariable UUID priceId) {
        List<PriceTierResponse> response = serviceService.listTiers(priceId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/prices/{priceId}/tiers/{tierId}")
    @Operation(summary = "Update a price tier")
    public ResponseEntity<ApiResponse<PriceTierResponse>> updateTier(
            @PathVariable UUID tierId,
            @Valid @RequestBody PriceTierUpdateRequest request) {
        PriceTierResponse response = serviceService.updateTier(tierId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Price tier updated successfully"));
    }

    @DeleteMapping("/prices/{priceId}/tiers/{tierId}")
    @Operation(summary = "Delete a price tier")
    public ResponseEntity<ApiResponse<Void>> deleteTier(@PathVariable UUID tierId) {
        serviceService.deleteTier(tierId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Price tier deleted successfully"));
    }
}
