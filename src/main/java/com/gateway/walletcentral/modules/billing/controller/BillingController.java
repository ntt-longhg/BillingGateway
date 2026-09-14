package com.gateway.walletcentral.modules.billing.controller;

import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.billing.dto.BillingWebhookRequest;
import com.gateway.walletcentral.modules.billing.dto.BillingWebhookResponse;
import com.gateway.walletcentral.modules.billing.service.BillingService;
import com.gateway.walletcentral.modules.tenant.model.Tenant;
import com.gateway.walletcentral.modules.tenant.repository.TenantRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/billing")
@Tag(name = "Billing", description = "Webhook billing endpoint for external services")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final BillingService billingService;
    private final TenantRepository tenantRepository;

    public BillingController(BillingService billingService, TenantRepository tenantRepository) {
        this.billingService = billingService;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping("/webhook")
    @Operation(summary = "Process billing webhook - authenticate via clientId:clientSecret in X-API-Key header")
    public ResponseEntity<ApiResponse<BillingWebhookResponse>> processWebhook(
            @Valid @RequestBody BillingWebhookRequest request,
            HttpServletRequest httpRequest) {

        // Extract tenant from X-API-Key header (clientId:clientSecret format)
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);
        log.info("Billing webhook request from API key: {}...", apiKey != null ? apiKey.substring(0, Math.min(apiKey.length(), 10)) : "null");

        Tenant tenant = extractTenantFromApiKey(apiKey);
        log.info("Authenticated tenant: {} ({})", tenant.getName(), tenant.getClientId());

        BillingWebhookResponse response = billingService.processBilling(tenant, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(response, "Billing processed successfully"));
    }

    private Tenant extractTenantFromApiKey(String apiKey) {
        if (apiKey == null || !apiKey.contains(":")) {
            throw new IllegalArgumentException("Invalid API key format. Expected: clientId:clientSecret");
        }
        String[] parts = apiKey.split(":", 2);
        String clientId = parts[0];

        return tenantRepository.findByClientId(clientId)
                .orElseThrow(() -> {
                    log.error("Tenant not found for clientId: {}", clientId);
                    return new com.gateway.walletcentral.core.exception.ResourceNotFoundException("Tenant", "clientId", clientId);
                });
    }
}
