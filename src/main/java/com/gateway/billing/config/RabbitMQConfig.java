package com.gateway.billing.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_TRANSACTION = "billing.transaction.exchange";
    public static final String EXCHANGE_WALLET = "billing.wallet.exchange";
    public static final String EXCHANGE_USAGE = "billing.usage.exchange";
    public static final String EXCHANGE_BILLING = "billing.billing.exchange";

    public static final String QUEUE_TRANSACTION_PROCESS = "billing.transaction.process";
    public static final String QUEUE_WALLET_PLAN_APPROVE = "billing.wallet.plan.approve";
    public static final String QUEUE_USAGE_LOG_RECORD = "billing.usagelog.record";
    public static final String QUEUE_BILLING_COMPLETED = "billing.completed.process";

    public static final String RK_TRANSACTION_CREATED = "transaction.created";
    public static final String RK_WALLET_PLAN_APPROVED = "wallet.plan.approved";
    public static final String RK_USAGE_LOG_RECORDED = "usage.log.recorded";
    public static final String RK_BILLING_COMPLETED = "billing.completed";

    public static final String QUEUE_DLQ = "billing.dlx";

    // ========== Exchanges ==========

    @Bean
    public TopicExchange transactionExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_TRANSACTION).durable(true).build();
    }

    @Bean
    public TopicExchange walletExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_WALLET).durable(true).build();
    }

    @Bean
    public TopicExchange usageExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_USAGE).durable(true).build();
    }

    @Bean
    public TopicExchange billingExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_BILLING).durable(true).build();
    }

    // ========== Queues ==========

    @Bean
    public Queue transactionProcessQueue() {
        return QueueBuilder.durable(QUEUE_TRANSACTION_PROCESS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue walletPlanApproveQueue() {
        return QueueBuilder.durable(QUEUE_WALLET_PLAN_APPROVE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue usageLogRecordQueue() {
        return QueueBuilder.durable(QUEUE_USAGE_LOG_RECORD)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Queue billingCompletedQueue() {
        return QueueBuilder.durable(QUEUE_BILLING_COMPLETED)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ)
                .build();
    }

    // ========== Bindings ==========

    @Bean
    public Binding transactionBinding() {
        return BindingBuilder.bind(transactionProcessQueue())
                .to(transactionExchange())
                .with(RK_TRANSACTION_CREATED);
    }

    @Bean
    public Binding walletPlanBinding() {
        return BindingBuilder.bind(walletPlanApproveQueue())
                .to(walletExchange())
                .with(RK_WALLET_PLAN_APPROVED);
    }

    @Bean
    public Binding usageLogBinding() {
        return BindingBuilder.bind(usageLogRecordQueue())
                .to(usageExchange())
                .with(RK_USAGE_LOG_RECORDED);
    }

    @Bean
    public Binding billingBinding() {
        return BindingBuilder.bind(billingCompletedQueue())
                .to(billingExchange())
                .with(RK_BILLING_COMPLETED);
    }

    // ========== Message Converter ==========

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
