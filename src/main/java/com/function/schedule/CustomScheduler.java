package com.function.schedule;

import com.common.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomScheduler {

    /**
     * 定时执行，初始化后5秒执行，结束后再隔100秒执行
     */
    @Scheduled(initialDelay = 5_000L, fixedDelay = 100_000L)
    public void doBySchedule() {
        log.info("【Scheduled】开始执行定时任务");
        MyUtil.sleep(10);
        log.info("【Scheduled】结束执行定时任务");
    }
}
