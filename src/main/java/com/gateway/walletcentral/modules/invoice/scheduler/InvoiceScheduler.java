package com.gateway.walletcentral.modules.invoice.scheduler;

import com.gateway.walletcentral.modules.invoice.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class InvoiceScheduler {

    private static final Logger log = LoggerFactory.getLogger(InvoiceScheduler.class);
    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final InvoiceService invoiceService;

    public InvoiceScheduler(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Auto-generate invoices for the previous month on the 1st of each month at 2:00 AM.
     * Cron: second minute hour day-of-month month day-of-week
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void generateMonthlyInvoices() {
        LocalDate previousMonth = LocalDate.now().minusMonths(1);
        String billingPeriod = previousMonth.format(PERIOD_FORMAT);

        log.info("========== INVOICE SCHEDULER START ========== Generating invoices for period: {}", billingPeriod);

        try {
            int count = invoiceService.generateAllInvoicesForPeriod(billingPeriod, "SYSTEM_SCHEDULER");
            log.info("========== INVOICE SCHEDULER END ========== Generated {} invoices for period: {}", count, billingPeriod);
        } catch (Exception e) {
            log.error("========== INVOICE SCHEDULER END ========== FAILED for period: {}", billingPeriod, e);
        }
    }
}
