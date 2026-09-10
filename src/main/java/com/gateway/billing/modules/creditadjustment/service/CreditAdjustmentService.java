package com.gateway.billing.modules.creditadjustment.service;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.cursor.CursorUtil;
import com.gateway.billing.core.exception.ResourceNotFoundException;
import com.gateway.billing.modules.creditadjustment.dto.*;
import com.gateway.billing.modules.creditadjustment.model.CreditAdjustment;
import com.gateway.billing.modules.creditadjustment.model.CreditAdjustmentType;
import com.gateway.billing.modules.creditadjustment.repository.CreditAdjustmentRepository;
import com.gateway.billing.modules.wallet.model.Wallet;
import com.gateway.billing.modules.wallet.repository.WalletRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CreditAdjustmentService {

    private final CreditAdjustmentRepository creditAdjustmentRepository;
    private final WalletRepository walletRepository;

    public CreditAdjustmentService(CreditAdjustmentRepository creditAdjustmentRepository,
                                   WalletRepository walletRepository) {
        this.creditAdjustmentRepository = creditAdjustmentRepository;
        this.walletRepository = walletRepository;
    }

    public CreditAdjustmentResponse create(CreditAdjustmentCreateRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "id", request.getWalletId()));

        BigDecimal creditLimitBefore = wallet.getCreditLimit();
        BigDecimal creditLimitAfter;

        creditLimitAfter = switch (request.getType()) {
            case INCREASE -> creditLimitBefore.add(request.getAdjustmentAmount());
            case DECREASE -> creditLimitBefore.subtract(request.getAdjustmentAmount());
            case SET -> request.getAdjustmentAmount();
        };

        CreditAdjustment adjustment = CreditAdjustment.builder()
                .wallet(wallet)
                .creditLimitBefore(creditLimitBefore)
                .creditLimitAfter(creditLimitAfter)
                .adjustmentAmount(request.getAdjustmentAmount())
                .type(request.getType())
                .reason(request.getReason())
                .referenceFrom(request.getReferenceFrom())
                .referenceId(request.getReferenceId())
                .createdBy(request.getCreatedBy())
                .build();

        wallet.setCreditLimit(creditLimitAfter);
        walletRepository.save(wallet);

        var saved = creditAdjustmentRepository.save(adjustment);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CursorPage<CreditAdjustmentResponse> list(UUID walletId, CreditAdjustmentType type, CursorParams params) {
        UUID cursorId = CursorUtil.parseCursor(params.getCursor());
        var pageable = PageRequest.of(0, params.getSize() + 1);

        var items = creditAdjustmentRepository.findWithCursor(cursorId, walletId, type, pageable)
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
    public CreditAdjustmentResponse getById(UUID id) {
        var adjustment = creditAdjustmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CreditAdjustment", "id", id));
        return toResponse(adjustment);
    }

    private CreditAdjustmentResponse toResponse(CreditAdjustment ca) {
        return CreditAdjustmentResponse.builder()
                .id(ca.getId())
                .walletId(ca.getWallet().getId())
                .creditLimitBefore(ca.getCreditLimitBefore())
                .creditLimitAfter(ca.getCreditLimitAfter())
                .adjustmentAmount(ca.getAdjustmentAmount())
                .type(ca.getType().name())
                .reason(ca.getReason())
                .referenceFrom(ca.getReferenceFrom())
                .referenceId(ca.getReferenceId())
                .createdBy(ca.getCreatedBy())
                .createdAt(ca.getCreatedAt())
                .build();
    }
}
