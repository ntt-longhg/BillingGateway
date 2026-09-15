package com.gateway.walletcentral.config;

import com.gateway.walletcentral.modules.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthService authService;

    public SchedulerConfig(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Cleanup expired OTPs and tokens every 30 minutes.
     * Prevents data accumulation from users who close browser without logging out.
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void cleanupExpiredAuthData() {
        int deletedOtps = authService.cleanupExpiredOtps();
        int deletedTokens = authService.cleanupExpiredTokens();
        if (deletedOtps > 0 || deletedTokens > 0) {
            log.info("Scheduled cleanup: removed {} expired OTPs, {} expired tokens", deletedOtps, deletedTokens);
        }
    }
}
