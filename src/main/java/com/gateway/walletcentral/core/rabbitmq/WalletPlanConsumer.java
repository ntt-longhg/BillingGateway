package com.gateway.walletcentral.core.rabbitmq;

import com.gateway.walletcentral.modules.wallet.model.Wallet;
import com.gateway.walletcentral.modules.wallet.repository.WalletRepository;
import com.gateway.walletcentral.modules.walletplan.model.WalletPlan;
import com.gateway.walletcentral.modules.walletplan.repository.WalletPlanRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class WalletPlanConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletPlanConsumer.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT.WALLETPLAN");

    private final WalletPlanRepository walletPlanRepository;
    private final WalletRepository walletRepository;

    public WalletPlanConsumer(WalletPlanRepository walletPlanRepository,
                              WalletRepository walletRepository) {
        this.walletPlanRepository = walletPlanRepository;
        this.walletRepository = walletRepository;
    }

    @RabbitListener(
            queues = "billing.wallet.plan.approve",
            executor = "virtualThreadExecutor"
    )
    public void handleWalletPlanApproved(Map<String, Object> message,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                          Channel channel) throws IOException {
        String walletPlanId = (String) message.get("walletPlanId");
        String tenantId = (String) message.get("tenantId");
        String walletId = (String) message.get("walletId");

        log.info("========== WALLET PLAN CONSUMER START ==========");
        log.info("WalletPlanId: {} | TenantId: {} | WalletId: {}", walletPlanId, tenantId, walletId);

        try {
            // 1. Reload wallet plan from DB for full details (with eagerly fetched tenant & pricingPlan)
            WalletPlan walletPlan = walletPlanRepository.findByIdWithRelations(UUID.fromString(walletPlanId)).orElse(null);
            if (walletPlan == null) {
                log.error("WalletPlan not found: {} - possible data inconsistency", walletPlanId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 2. Verify wallet state
            Wallet wallet = walletRepository.findById(UUID.fromString(walletId)).orElse(null);
            if (wallet != null) {
                log.info("Wallet state after approval: balance={} creditLimit={} available={}",
                        wallet.getBalance(), wallet.getCreditLimit(), wallet.getAvailableBalance());

                // Verify balance matches wallet plan expectation
                if (walletPlan.getBalanceAfter().compareTo(wallet.getBalance()) != 0) {
                    log.warn("BALANCE MISMATCH after approval! Plan expects: {} | Actual: {} | WalletPlanId={}",
                            walletPlan.getBalanceAfter(), wallet.getBalance(), walletPlanId);
                }
            }

            // 3. Audit log
            auditLog.info("PLAN_ID={} | TENANT={} | PLAN={} | PRICE={} | BONUS={} | CREDITED={} | " +
                            "BALANCE_BEFORE={} | BALANCE_AFTER={} | CREDIT_BEFORE={} | CREDIT_AFTER={} | " +
                            "APPROVED_BY={}",
                    walletPlan.getId(),
                    walletPlan.getTenant().getName(),
                    walletPlan.getPricingPlan().getName(),
                    walletPlan.getPrice(),
                    walletPlan.getBonusAmount(),
                    walletPlan.getCreditedAmount(),
                    walletPlan.getBalanceBefore(),
                    walletPlan.getBalanceAfter(),
                    walletPlan.getCreditLimitBefore(),
                    walletPlan.getCreditLimitAfter(),
                    walletPlan.getApprovedBy());

            // 4. Log credit limit change
            if (walletPlan.getCreditLimitBefore().compareTo(walletPlan.getCreditLimitAfter()) != 0) {
                log.info("Credit limit changed: {} -> {} (diff={}) for tenant={}",
                        walletPlan.getCreditLimitBefore(),
                        walletPlan.getCreditLimitAfter(),
                        walletPlan.getCreditLimitAfter().subtract(walletPlan.getCreditLimitBefore()),
                        walletPlan.getTenant().getName());
            }

            log.info("========== WALLET PLAN CONSUMER END ========== SUCCESS plan={}", walletPlanId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("========== WALLET PLAN CONSUMER END ========== FAILED plan={}", walletPlanId, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
