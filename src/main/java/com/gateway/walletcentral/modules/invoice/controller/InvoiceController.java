package com.gateway.walletcentral.modules.invoice.controller;

import com.gateway.walletcentral.core.cursor.CursorPage;
import com.gateway.walletcentral.core.cursor.CursorParams;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.invoice.dto.*;
import com.gateway.walletcentral.modules.invoice.model.InvoiceStatus;
import com.gateway.walletcentral.modules.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoice", description = "Invoice management operations")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(@Valid @RequestBody InvoiceCreateRequest request) {
        InvoiceResponse response = invoiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Invoice created successfully"));
    }

    @GetMapping
    @Operation(summary = "List invoices with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<InvoiceResponse>>> list(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) InvoiceStatus status,
            @ModelAttribute CursorParams params) {
        CursorPage<InvoiceResponse> response = invoiceService.list(tenantId, status, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(@PathVariable UUID id) {
        InvoiceResponse response = invoiceService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Mark invoice as paid")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markAsPaid(
            @PathVariable UUID id,
            @Valid @RequestBody InvoicePayRequest request) {
        InvoiceResponse response = invoiceService.markAsPaid(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Invoice marked as paid"));
    }
}
