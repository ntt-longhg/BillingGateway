package com.gateway.walletcentral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("virtualThreadExecutor")
    public TaskExecutor virtualThreadExecutor() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        return new TaskExecutor() {
            @Override
            public void execute(Runnable task) {
                executorService.execute(task);
            }
        };
    }
}
