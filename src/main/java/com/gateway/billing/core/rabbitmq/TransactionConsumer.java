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
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    @RabbitListener(
            queues = "billing.transaction.process",
            executor = "virtualThreadExecutor"
    )
    public void handleTransactionCreated(Map<String, Object> message,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                         Channel channel) throws IOException {
        try {
            log.info("Processing transaction event on virtual thread: {}", Thread.currentThread());
            log.info("Transaction payload: {}", message);

            // TODO: Business logic
            // 1. Verify wallet balance
            // 2. Execute transaction
            // 3. Update wallet balance
            // 4. Record audit log

            channel.basicAck(deliveryTag, false);
            log.info("Transaction event processed successfully");
        } catch (Exception e) {
            log.error("Failed to process transaction event", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
