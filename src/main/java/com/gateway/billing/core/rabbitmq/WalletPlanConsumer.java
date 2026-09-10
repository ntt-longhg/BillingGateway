package com.gateway.billing.core.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class WalletPlanConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletPlanConsumer.class);

    @RabbitListener(
            queues = "billing.wallet.plan.approve",
            executor = "virtualThreadExecutor"
    )
    public void handleWalletPlanApproved(Map<String, Object> message,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                          Channel channel) throws IOException {
        try {
            log.info("Processing wallet plan approval on virtual thread: {}", Thread.currentThread());
            log.info("Wallet plan payload: {}", message);

            // TODO: Business logic
            // 1. Update wallet balance
            // 2. Create credit adjustment record
            // 3. Create transaction record
            // 4. Notify tenant

            channel.basicAck(deliveryTag, false);
            log.info("Wallet plan approval processed successfully");
        } catch (Exception e) {
            log.error("Failed to process wallet plan approval", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
