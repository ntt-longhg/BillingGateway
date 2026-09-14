package com.gateway.billing.modules.invoice.service;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.cursor.CursorUtil;
import com.gateway.billing.core.exception.BusinessException;
import com.gateway.billing.core.exception.ResourceNotFoundException;
import com.gateway.billing.modules.invoice.dto.*;
import com.gateway.billing.modules.invoice.model.Invoice;
import com.gateway.billing.modules.invoice.model.InvoiceStatus;
import com.gateway.billing.modules.invoice.repository.InvoiceRepository;
import com.gateway.billing.modules.tenant.repository.TenantRepository;
import com.gateway.billing.modules.wallet.repository.WalletRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.OffsetDateTime;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TenantRepository tenantRepository;
    private final WalletRepository walletRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
            TenantRepository tenantRepository,
            WalletRepository walletRepository) {
        this.invoiceRepository = invoiceRepository;
        this.tenantRepository = tenantRepository;
        this.walletRepository = walletRepository;
    }

    public InvoiceResponse create(InvoiceCreateRequest request) {
        if (invoiceRepository.existsByTenantIdAndBillingPeriod(request.getTenantId(), request.getBillingPeriod())) {
            throw new BusinessException("DUPLICATE_INVOICE", "Invoice already exists for tenant "
                    + request.getTenantId() + " and period " + request.getBillingPeriod());
        }

        var tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", request.getTenantId()));

        var wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", request.getWalletId()));

        Invoice invoice = Invoice.builder()
                .tenant(tenant)
                .wallet(wallet)
                .billingPeriod(request.getBillingPeriod())
                .totalAmount(request.getTotalAmount())
                .status(InvoiceStatus.ISSUED)
                .dueDate(request.getDueDate())
                .createdAt(OffsetDateTime.now())
                .updatedBy(request.getUpdatedBy())
                .build();

        var saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CursorPage<InvoiceResponse> list(UUID tenantId, InvoiceStatus status, CursorParams params) {
        UUID cursorId = CursorUtil.parseCursor(params.getCursor());
        var pageable = PageRequest.of(0, params.getSize() + 1);

        var items = invoiceRepository.findWithCursor(cursorId, tenantId, status, pageable)
                .stream()
                .map(this::toResponse)
                .toList();

        boolean hasNext = items.size() > params.getSize();
        if (hasNext) {
            items = items.subList(0, params.getSize());
        }
        String nextCursor = hasNext && !items.isEmpty() ? items.getLast().getId().toString() : null;

        return CursorPage.of(items, nextCursor, hasNext, params.getSize());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(UUID id) {
        var invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return toResponse(invoice);
    }

    public InvoiceResponse markAsPaid(UUID id, InvoicePayRequest request) {
        var invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("ALREADY_PAID", "Invoice is already paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setUpdatedBy(request.getUpdatedBy());
        invoice.setUpdatedAt(OffsetDateTime.now());

        var saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    private InvoiceResponse toResponse(Invoice i) {
        return InvoiceResponse.builder()
                .id(i.getId())
                .tenantId(i.getTenant().getId())
                .tenantName(i.getTenant().getName())
                .walletId(i.getWallet().getId())
                .billingPeriod(i.getBillingPeriod())
                .totalAmount(i.getTotalAmount())
                .status(i.getStatus().name())
                .dueDate(i.getDueDate())
                .updatedBy(i.getUpdatedBy())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
