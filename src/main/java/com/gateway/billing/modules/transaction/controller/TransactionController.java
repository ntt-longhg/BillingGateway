package com.gateway.billing.modules.transaction.controller;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.response.ApiResponse;
import com.gateway.billing.modules.transaction.dto.*;
import com.gateway.billing.modules.transaction.model.TransactionStatus;
import com.gateway.billing.modules.transaction.model.TransactionType;
import com.gateway.billing.modules.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction", description = "Transaction management operations")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(summary = "Create a new transaction (deposit, charge, refund)")
    public ResponseEntity<ApiResponse<TransactionResponse>> create(@Valid @RequestBody TransactionCreateRequest request) {
        TransactionResponse response = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Transaction created successfully"));
    }

    @GetMapping
    @Operation(summary = "List transactions with cursor pagination")
    public ResponseEntity<ApiResponse<CursorPage<TransactionResponse>>> list(
            @RequestParam(required = false) UUID walletId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @ModelAttribute CursorParams params) {
        CursorPage<TransactionResponse> response = transactionService.list(walletId, type, status, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@PathVariable UUID id) {
        TransactionResponse response = transactionService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "List transactions by wallet ID")
    public ResponseEntity<ApiResponse<CursorPage<TransactionResponse>>> listByWallet(
            @PathVariable UUID walletId,
            @ModelAttribute CursorParams params) {
        CursorPage<TransactionResponse> response = transactionService.listByWallet(walletId, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
