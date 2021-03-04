package com.denemeler.webreactiverest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class WebreactiverestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebreactiverestApplication.class, args);
    }


    @Bean(name = "subscriberTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("subscriber-");
        executor.initialize();
        return executor;
    }

}
