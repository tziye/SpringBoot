package com.core.util;

import com.core.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.concurrent.TimeUnit;

@Slf4j
class BackOffTest extends ApplicationTest {

    @Test
    void exponential() throws InterruptedException {
        ExponentialBackOff backOff = new ExponentialBackOff();
        // 初始间隔
        backOff.setInitialInterval(2000);
        // 每次间隔增长率
        backOff.setMultiplier(1.5);
        // 最大间隔，达到后以此为间隔直到最大累计间隔
        backOff.setMaxInterval(10000);
        // 最大累计间隔
        backOff.setMaxElapsedTime(60000);

        BackOffExecution exec = backOff.start();

        long waitInterval = exec.nextBackOff();
        int i = 1;
        do {
            TimeUnit.MILLISECONDS.sleep(waitInterval);
            log.info("等待：{}，重试：{}", waitInterval, i++);
            waitInterval = exec.nextBackOff();
        } while (waitInterval != BackOffExecution.STOP);
    }

    @Test
    void fixed() throws InterruptedException {
        FixedBackOff backOff = new FixedBackOff();
        backOff.setInterval(2000);
        backOff.setMaxAttempts(5);

        BackOffExecution exec = backOff.start();

        long waitInterval = exec.nextBackOff();
        int i = 1;
        do {
            TimeUnit.MILLISECONDS.sleep(waitInterval);
            log.info("等待：{}，重试：{}", waitInterval, i++);
            waitInterval = exec.nextBackOff();
        } while (waitInterval != BackOffExecution.STOP);
    }

}
