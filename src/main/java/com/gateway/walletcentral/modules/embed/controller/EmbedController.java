package com.gateway.walletcentral.modules.embed.controller;

import com.gateway.walletcentral.core.cursor.CursorPage;
import com.gateway.walletcentral.core.cursor.CursorParams;
import com.gateway.walletcentral.core.response.ApiResponse;
import com.gateway.walletcentral.modules.creditadjustment.dto.CreditAdjustmentResponse;
import com.gateway.walletcentral.modules.creditadjustment.model.CreditAdjustmentType;
import com.gateway.walletcentral.modules.creditadjustment.service.CreditAdjustmentService;
import com.gateway.walletcentral.modules.invoice.dto.InvoiceResponse;
import com.gateway.walletcentral.modules.invoice.model.InvoiceStatus;
import com.gateway.walletcentral.modules.invoice.service.InvoiceService;
import com.gateway.walletcentral.modules.pricingplan.model.PricingPlan;
import com.gateway.walletcentral.modules.pricingplan.model.PricingPlanStatus;
import com.gateway.walletcentral.modules.pricingplan.model.PricingPlanType;
import com.gateway.walletcentral.modules.pricingplan.repository.PricingPlanRepository;
import com.gateway.walletcentral.modules.tenant.model.Tenant;
import com.gateway.walletcentral.modules.tenant.repository.TenantRepository;
import com.gateway.walletcentral.modules.transaction.dto.TransactionResponse;
import com.gateway.walletcentral.modules.transaction.model.TransactionStatus;
import com.gateway.walletcentral.modules.transaction.model.TransactionType;
import com.gateway.walletcentral.modules.transaction.service.TransactionService;
import com.gateway.walletcentral.modules.usagelog.dto.UsageLogResponse;
import com.gateway.walletcentral.modules.usagelog.service.UsageLogService;
import com.gateway.walletcentral.modules.wallet.dto.WalletResponse;
import com.gateway.walletcentral.modules.wallet.model.Wallet;
import com.gateway.walletcentral.modules.wallet.repository.WalletRepository;
import com.gateway.walletcentral.modules.wallet.service.WalletService;
import com.gateway.walletcentral.modules.walletplan.dto.WalletPlanCreateRequest;
import com.gateway.walletcentral.modules.walletplan.dto.WalletPlanResponse;
import com.gateway.walletcentral.modules.walletplan.service.WalletPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/embed")
@Tag(name = "Embed", description = "Tenant-scoped endpoints for embedded views (data filtered by X-API-Key)")
public class EmbedController {

    private static final Logger log = LoggerFactory.getLogger(EmbedController.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final TenantRepository tenantRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final InvoiceService invoiceService;
    private final UsageLogService usageLogService;
    private final CreditAdjustmentService creditAdjustmentService;
    private final WalletPlanService walletPlanService;
    private final PricingPlanRepository pricingPlanRepository;

    public EmbedController(TenantRepository tenantRepository,
                           WalletRepository walletRepository,
                           WalletService walletService,
                           TransactionService transactionService,
                           InvoiceService invoiceService,
                           UsageLogService usageLogService,
                           CreditAdjustmentService creditAdjustmentService,
                           WalletPlanService walletPlanService,
                           PricingPlanRepository pricingPlanRepository) {
        this.tenantRepository = tenantRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.invoiceService = invoiceService;
        this.usageLogService = usageLogService;
        this.creditAdjustmentService = creditAdjustmentService;
        this.walletPlanService = walletPlanService;
        this.pricingPlanRepository = pricingPlanRepository;
    }

    @GetMapping("/wallet")
    @Operation(summary = "Get wallet for the current tenant (from X-API-Key)")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(HttpServletRequest request) {
        Tenant tenant = extractTenant(request);
        WalletResponse response = walletService.getByTenantId(tenant.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/transactions")
    @Operation(summary = "List transactions for the current tenant's wallet")
    public ResponseEntity<ApiResponse<CursorPage<TransactionResponse>>> listTransactions(
            HttpServletRequest request,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @ModelAttribute CursorParams params) {
        Tenant tenant = extractTenant(request);
        Wallet wallet = walletRepository.findByTenantId(tenant.getId())
                .orElseThrow(() -> new com.gateway.walletcentral.core.exception.ResourceNotFoundException("Wallet", "tenantId", tenant.getId()));
        CursorPage<TransactionResponse> response = transactionService.listByWallet(wallet.getId(), params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/invoices")
    @Operation(summary = "List invoices for the current tenant")
    public ResponseEntity<ApiResponse<CursorPage<InvoiceResponse>>> listInvoices(
            HttpServletRequest request,
            @RequestParam(required = false) InvoiceStatus status,
            @ModelAttribute CursorParams params) {
        Tenant tenant = extractTenant(request);
        CursorPage<InvoiceResponse> response = invoiceService.list(tenant.getId(), status, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/usage-logs")
    @Operation(summary = "List usage logs for the current tenant")
    public ResponseEntity<ApiResponse<CursorPage<UsageLogResponse>>> listUsageLogs(
            HttpServletRequest request,
            @ModelAttribute CursorParams params) {
        Tenant tenant = extractTenant(request);
        CursorPage<UsageLogResponse> response = usageLogService.list(tenant.getId(), null, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/credit-adjustments")
    @Operation(summary = "List credit adjustments for the current tenant's wallet")
    public ResponseEntity<ApiResponse<CursorPage<CreditAdjustmentResponse>>> listCreditAdjustments(
            HttpServletRequest request,
            @RequestParam(required = false) CreditAdjustmentType type,
            @ModelAttribute CursorParams params) {
        Tenant tenant = extractTenant(request);
        Wallet wallet = walletRepository.findByTenantId(tenant.getId())
                .orElseThrow(() -> new com.gateway.walletcentral.core.exception.ResourceNotFoundException("Wallet", "tenantId", tenant.getId()));
        CursorPage<CreditAdjustmentResponse> response = creditAdjustmentService.list(wallet.getId(), type, params);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/pricing-plans")
    @Operation(summary = "List active pricing plans available for the current tenant's wallet type")
    public ResponseEntity<ApiResponse<List<PricingPlan>>> listPricingPlans(HttpServletRequest request) {
        Tenant tenant = extractTenant(request);
        Wallet wallet = walletRepository.findByTenantId(tenant.getId())
                .orElseThrow(() -> new com.gateway.walletcentral.core.exception.ResourceNotFoundException("Wallet", "tenantId", tenant.getId()));

        List<PricingPlan> plans = pricingPlanRepository.findAll().stream()
                .filter(p -> p.getStatus() == PricingPlanStatus.ACTIVE)
                .filter(p -> {
                    if (wallet.getType() == com.gateway.walletcentral.modules.wallet.model.WalletType.POSTPAID) {
                        return p.getType() == PricingPlanType.CREDIT_INCREASE;
                    } else {
                        return p.getType() == PricingPlanType.BALANCE_TOPUP;
                    }
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(plans));
    }

    @PostMapping("/wallet-plans")
    @Operation(summary = "Create a wallet plan purchase request for the current tenant")
    public ResponseEntity<ApiResponse<WalletPlanResponse>> createWalletPlan(
            HttpServletRequest request,
            @RequestBody UUID pricingPlanId) {
        Tenant tenant = extractTenant(request);
        WalletPlanCreateRequest planRequest = WalletPlanCreateRequest.builder()
                .tenantId(tenant.getId())
                .pricingPlanId(pricingPlanId)
                .createdBy("embed:" + tenant.getClientId())
                .build();
        WalletPlanResponse response = walletPlanService.create(planRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Wallet plan created successfully"));
    }

    private Tenant extractTenant(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || !apiKey.contains(":")) {
            throw new IllegalArgumentException("Invalid API key format");
        }
        String clientId = apiKey.split(":", 2)[0];
        return tenantRepository.findByClientId(clientId)
                .orElseThrow(() -> new com.gateway.walletcentral.core.exception.ResourceNotFoundException("Tenant", "clientId", clientId));
    }
}
