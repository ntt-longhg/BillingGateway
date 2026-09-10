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
public class UsageLogConsumer {

    private static final Logger log = LoggerFactory.getLogger(UsageLogConsumer.class);

    @RabbitListener(
            queues = "billing.usagelog.record",
            executor = "virtualThreadExecutor"
    )
    public void handleUsageLogRecorded(Map<String, Object> message,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                        Channel channel) throws IOException {
        try {
            log.info("Processing usage log event on virtual thread: {}", Thread.currentThread());
            log.info("Usage log payload: {}", message);

            // TODO: Business logic
            // 1. Calculate fees from service pricing
            // 2. Deduct from wallet balance
            // 3. Create transaction record
            // 4. Create usage log record

            channel.basicAck(deliveryTag, false);
            log.info("Usage log event processed successfully");
        } catch (Exception e) {
            log.error("Failed to process usage log event", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
