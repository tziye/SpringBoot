package com.function.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时配置
 */
@Slf4j
@EnableScheduling
@Configuration
public class ScheduleConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        log.info("Setting up thread pool task scheduler with 20 threads.");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("Scheduler-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setErrorHandler(t -> log.error("定时任务出现异常", t));
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

}
