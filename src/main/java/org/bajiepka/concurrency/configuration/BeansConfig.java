package org.bajiepka.concurrency.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

@Configuration
public class BeansConfig {

    @Bean(name = "single_scheduled_executor")
    ScheduledExecutorService singleScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean(name = "multi_scheduled_executor")
    ScheduledExecutorService multiScheduledExecutorService() {
        return Executors.newScheduledThreadPool(10);
    }

    @Bean
    Semaphore semaphore() {
        return new Semaphore(3);
    }

}
