package com.gateway.billing.core.rabbitmq;

import com.gateway.billing.modules.usagelog.model.UsageLog;
import com.gateway.billing.modules.usagelog.repository.UsageLogRepository;
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
public class UsageLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(UsageLogConsumer.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT.USAGELOG");

    private final UsageLogRepository usageLogRepository;

    public UsageLogConsumer(UsageLogRepository usageLogRepository) {
        this.usageLogRepository = usageLogRepository;
    }

    @RabbitListener(
            queues = "billing.usagelog.record",
            executor = "virtualThreadExecutor"
    )
    public void handleUsageLogRecorded(Map<String, Object> message,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                        Channel channel) throws IOException {
        String usageLogId = (String) message.get("usageLogId");
        String tenantId = (String) message.get("tenantId");
        String serviceId = (String) message.get("serviceId");

        log.info("========== USAGE LOG CONSUMER START ==========");
        log.info("UsageLogId: {} | TenantId: {} | ServiceId: {}", usageLogId, tenantId, serviceId);

        try {
            // 1. Reload usage log from DB for full details
            UsageLog usageLog = usageLogRepository.findById(UUID.fromString(usageLogId)).orElse(null);
            if (usageLog == null) {
                log.error("UsageLog not found: {} - possible data inconsistency", usageLogId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 2. Audit log
            String serviceName = usageLog.getService() != null ? usageLog.getService().getCode() : "TOPUP";
            String tenantName = usageLog.getTenant() != null ? usageLog.getTenant().getName() : "unknown";

            auditLog.info("USAGE_ID={} | TENANT={} | SERVICE={} | USAGE_UNITS={} | CHARGED={} | WALLET_TYPE={} | BALANCE_SNAPSHOT={}",
                    usageLog.getId(),
                    tenantName,
                    serviceName,
                    usageLog.getTotalUsage(),
                    usageLog.getTotalCharged(),
                    usageLog.getWalletTypeSnapshot(),
                    usageLog.getAvailableBalanceSnapshot());

            // 3. Log fee breakdown details
            if (usageLog.getFeeBreakdown() != null) {
                log.info("Fee breakdown: strategy={} initialFee={} subsequentFee={}",
                        usageLog.getFeeBreakdown().getStrategy(),
                        usageLog.getFeeBreakdown().getInitialFeeApplied(),
                        usageLog.getFeeBreakdown().getSubsequentFeeApplied());

                if (usageLog.getFeeBreakdown().getRawCalculationDetails() != null) {
                    log.info("Raw calculation: {}", usageLog.getFeeBreakdown().getRawCalculationDetails());
                }
            }

            // 4. High usage alert
            if (usageLog.getTotalUsage() > 10000) {
                log.warn("HIGH USAGE ALERT: tenant={} service={} usage={} charged={}",
                        tenantName, serviceName, usageLog.getTotalUsage(), usageLog.getTotalCharged());
            }

            // 5. High charge alert
            if (usageLog.getTotalCharged().compareTo(new java.math.BigDecimal("500000")) > 0) {
                log.warn("HIGH CHARGE ALERT: tenant={} service={} charged={}",
                        tenantName, serviceName, usageLog.getTotalCharged());
            }

            log.info("========== USAGE LOG CONSUMER END ========== SUCCESS usageLog={}", usageLogId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("========== USAGE LOG CONSUMER END ========== FAILED usageLog={}", usageLogId, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
