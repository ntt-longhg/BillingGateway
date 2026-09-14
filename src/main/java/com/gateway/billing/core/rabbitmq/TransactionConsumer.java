package com.gateway.billing.core.rabbitmq;

import com.gateway.billing.modules.transaction.model.Transaction;
import com.gateway.billing.modules.transaction.repository.TransactionRepository;
import com.gateway.billing.modules.wallet.model.Wallet;
import com.gateway.billing.modules.wallet.repository.WalletRepository;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT.TRANSACTION");

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionConsumer(TransactionRepository transactionRepository,
                               WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @RabbitListener(
            queues = "billing.transaction.process",
            executor = "virtualThreadExecutor"
    )
    public void handleTransactionCreated(Map<String, Object> message,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                         Channel channel) throws IOException {
        String transactionId = (String) message.get("transactionId");
        String walletId = (String) message.get("walletId");
        String type = (String) message.get("type");
        Object amountObj = message.get("amount");
        Object balanceAfterObj = message.get("balanceAfter");

        log.info("========== TRANSACTION CONSUMER START ==========");
        log.info("TransactionId: {} | WalletId: {} | Type: {}", transactionId, walletId, type);

        try {
            // 1. Reload transaction from DB for full details
            Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId)).orElse(null);
            if (transaction == null) {
                log.error("Transaction not found: {} - possible data inconsistency", transactionId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 2. Verify wallet balance consistency
            Wallet wallet = transaction.getWallet();
            BigDecimal expectedBalance = transaction.getBalanceAfter();
            BigDecimal actualBalance = wallet.getBalance();

            if (expectedBalance.compareTo(actualBalance) != 0) {
                log.warn("BALANCE MISMATCH! TransactionId: {} | Expected: {} | Actual: {} | WalletId: {}",
                        transactionId, expectedBalance, actualBalance, wallet.getId());
                // In production: trigger alert, create compensation record
            } else {
                log.info("Balance verified OK: {} == {} for wallet={}", expectedBalance, actualBalance, wallet.getId());
            }

            // 3. Audit log
            auditLog.info("TXN_ID={} | WALLET_ID={} | TYPE={} | AMOUNT={} | BALANCE_BEFORE={} | BALANCE_AFTER={} | STATUS={} | REF={} | REF_ID={}",
                    transaction.getId(),
                    wallet.getId(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getBalanceBefore(),
                    transaction.getBalanceAfter(),
                    transaction.getStatus(),
                    transaction.getReferenceFrom(),
                    transaction.getReferenceId());

            // 4. Log large transactions for monitoring
            if (transaction.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
                log.warn("LARGE TRANSACTION ALERT: id={} amount={} wallet={} tenant={}",
                        transaction.getId(), transaction.getAmount(),
                        wallet.getId(), wallet.getTenant().getName());
            }

            log.info("========== TRANSACTION CONSUMER END ========== SUCCESS transaction={}", transactionId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("========== TRANSACTION CONSUMER END ========== FAILED transaction={}", transactionId, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
