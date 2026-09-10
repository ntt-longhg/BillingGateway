package com.gateway.billing.modules.walletplan.service;

import com.gateway.billing.core.cursor.CursorPage;
import com.gateway.billing.core.cursor.CursorParams;
import com.gateway.billing.core.cursor.CursorUtil;
import com.gateway.billing.core.exception.BusinessException;
import com.gateway.billing.core.exception.ResourceNotFoundException;
import com.gateway.billing.core.rabbitmq.MessageProducer;
import com.gateway.billing.modules.pricingplan.model.BonusType;
import com.gateway.billing.modules.pricingplan.model.CreditLimitAction;
import com.gateway.billing.modules.pricingplan.model.PricingPlan;
import com.gateway.billing.modules.pricingplan.repository.PricingPlanRepository;
import com.gateway.billing.modules.tenant.model.Tenant;
import com.gateway.billing.modules.tenant.repository.TenantRepository;
import com.gateway.billing.modules.wallet.model.Wallet;
import com.gateway.billing.modules.wallet.repository.WalletRepository;
import com.gateway.billing.modules.walletplan.dto.*;
import com.gateway.billing.modules.walletplan.model.WalletPlan;
import com.gateway.billing.modules.walletplan.model.WalletPlanStatus;
import com.gateway.billing.modules.walletplan.repository.WalletPlanRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class WalletPlanService {

    private final WalletPlanRepository walletPlanRepository;
    private final TenantRepository tenantRepository;
    private final PricingPlanRepository pricingPlanRepository;
    private final WalletRepository walletRepository;
    private final MessageProducer messageProducer;

    public WalletPlanService(WalletPlanRepository walletPlanRepository,
                             TenantRepository tenantRepository,
                             PricingPlanRepository pricingPlanRepository,
                             WalletRepository walletRepository,
                             MessageProducer messageProducer) {
        this.walletPlanRepository = walletPlanRepository;
        this.tenantRepository = tenantRepository;
        this.pricingPlanRepository = pricingPlanRepository;
        this.walletRepository = walletRepository;
        this.messageProducer = messageProducer;
    }

    public WalletPlanResponse create(WalletPlanCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", request.getTenantId()));

        PricingPlan pricingPlan = pricingPlanRepository.findById(request.getPricingPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("PricingPlan", "id", request.getPricingPlanId()));

        Wallet wallet = walletRepository.findByTenantId(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "tenantId", request.getTenantId()));

        WalletPlan walletPlan = WalletPlan.builder()
                .pricingPlan(pricingPlan)
                .tenant(tenant)
                .price(pricingPlan.getPrice())
                .bonusAmount(BigDecimal.ZERO)
                .creditedAmount(BigDecimal.ZERO)
                .balanceBefore(wallet.getBalance())
                .balanceAfter(wallet.getBalance())
                .creditLimitBefore(wallet.getCreditLimit())
                .creditLimitAfter(wallet.getCreditLimit())
                .status(WalletPlanStatus.PENDING)
                .createdBy(request.getCreatedBy())
                .build();

        var saved = walletPlanRepository.save(walletPlan);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CursorPage<WalletPlanResponse> list(UUID tenantId, WalletPlanStatus status, CursorParams params) {
        UUID cursorId = CursorUtil.parseCursor(params.getCursor());
        var pageable = PageRequest.of(0, params.getSize() + 1);

        var items = walletPlanRepository.findWithCursor(cursorId, tenantId, status, pageable)
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
    public WalletPlanResponse getById(UUID id) {
        var walletPlan = walletPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WalletPlan", "id", id));
        return toResponse(walletPlan);
    }

    public WalletPlanResponse approve(UUID id, WalletPlanApproveRequest request) {
        WalletPlan walletPlan = walletPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WalletPlan", "id", id));

        if (walletPlan.getStatus() != WalletPlanStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Wallet plan must be PENDING to approve");
        }

        Wallet wallet = walletRepository.findByTenantId(walletPlan.getTenant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "tenantId", walletPlan.getTenant().getId()));

        PricingPlan plan = walletPlan.getPricingPlan();
        BigDecimal bonusAmount = calculateBonus(plan, walletPlan.getPrice());
        BigDecimal creditedAmount = walletPlan.getPrice().add(bonusAmount);

        walletPlan.setBonusAmount(bonusAmount);
        walletPlan.setCreditedAmount(creditedAmount);
        walletPlan.setBalanceBefore(wallet.getBalance());
        walletPlan.setBalanceAfter(wallet.getBalance().add(creditedAmount));
        walletPlan.setCreditLimitBefore(wallet.getCreditLimit());

        BigDecimal newCreditLimit = calculateNewCreditLimit(plan, wallet.getCreditLimit());
        walletPlan.setCreditLimitAfter(newCreditLimit);
        walletPlan.setStatus(WalletPlanStatus.APPROVE);
        walletPlan.setApprovedAt(OffsetDateTime.now());
        walletPlan.setApprovedBy(request.getApprovedBy());

        wallet.setBalance(wallet.getBalance().add(creditedAmount));
        wallet.setCreditLimit(newCreditLimit);

        walletRepository.save(wallet);
        var saved = walletPlanRepository.save(walletPlan);

        Map<String, Object> event = new HashMap<>();
        event.put("walletPlanId", saved.getId().toString());
        event.put("tenantId", walletPlan.getTenant().getId().toString());
        event.put("walletId", wallet.getId().toString());
        event.put("creditedAmount", creditedAmount);
        event.put("newBalance", wallet.getBalance());
        event.put("newCreditLimit", wallet.getCreditLimit());
        messageProducer.publishWalletPlanApproved(event);

        return toResponse(saved);
    }

    public WalletPlanResponse reject(UUID id, WalletPlanApproveRequest request) {
        WalletPlan walletPlan = walletPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WalletPlan", "id", id));

        if (walletPlan.getStatus() != WalletPlanStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS", "Wallet plan must be PENDING to reject");
        }

        walletPlan.setStatus(WalletPlanStatus.REJECT);
        walletPlan.setApprovedAt(OffsetDateTime.now());
        walletPlan.setApprovedBy(request.getApprovedBy());

        var saved = walletPlanRepository.save(walletPlan);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CursorPage<WalletPlanResponse> listPending(CursorParams params) {
        var items = walletPlanRepository.findByStatusOrderByCreatedAtAsc(WalletPlanStatus.PENDING)
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

    private BigDecimal calculateBonus(PricingPlan plan, BigDecimal price) {
        if (plan.getBonusType() == BonusType.PERCENTAGE && plan.getBonusValue() != null) {
            return price.multiply(plan.getBonusValue()).divide(BigDecimal.valueOf(100));
        } else if (plan.getBonusType() == BonusType.FIXED_AMOUNT) {
            return plan.getBonusValue() != null ? plan.getBonusValue() : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateNewCreditLimit(PricingPlan plan, BigDecimal currentCreditLimit) {
        return switch (plan.getCreditLimitAction()) {
            case INCREASE -> currentCreditLimit.add(plan.getCreditLimitValue());
            case SET -> plan.getCreditLimitValue();
            case NONE -> currentCreditLimit;
        };
    }

    private WalletPlanResponse toResponse(WalletPlan wp) {
        return WalletPlanResponse.builder()
                .id(wp.getId())
                .tenantId(wp.getTenant().getId())
                .tenantName(wp.getTenant().getName())
                .pricingPlanId(wp.getPricingPlan().getId())
                .pricingPlanName(wp.getPricingPlan().getName())
                .price(wp.getPrice())
                .bonusAmount(wp.getBonusAmount())
                .creditedAmount(wp.getCreditedAmount())
                .balanceBefore(wp.getBalanceBefore())
                .balanceAfter(wp.getBalanceAfter())
                .creditLimitBefore(wp.getCreditLimitBefore())
                .creditLimitAfter(wp.getCreditLimitAfter())
                .status(wp.getStatus().name())
                .approvedAt(wp.getApprovedAt())
                .approvedBy(wp.getApprovedBy())
                .createdBy(wp.getCreatedBy())
                .createdAt(wp.getCreatedAt())
                .build();
    }
}
