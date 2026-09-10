package com.gateway.billing.core.rabbitmq;

import com.gateway.billing.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTransactionCreated(Map<String, Object> payload) {
        log.info("Publishing transaction.created event: {}", payload);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_TRANSACTION,
                RabbitMQConfig.RK_TRANSACTION_CREATED,
                payload
        );
    }

    public void publishWalletPlanApproved(Map<String, Object> payload) {
        log.info("Publishing wallet.plan.approved event: {}", payload);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_WALLET,
                RabbitMQConfig.RK_WALLET_PLAN_APPROVED,
                payload
        );
    }

    public void publishUsageLogRecorded(Map<String, Object> payload) {
        log.info("Publishing usage.log.recorded event: {}", payload);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_USAGE,
                RabbitMQConfig.RK_USAGE_LOG_RECORDED,
                payload
        );
    }
}
