package com.core.util;

import com.core.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

@Slf4j
class StopWatchTest extends ApplicationTest {

    @Test
    void testStopWatch() throws InterruptedException {
        StopWatch stopWatch = new StopWatch("snail");
        stopWatch.start("snail_task1");
        TimeUnit.SECONDS.sleep(1);
        stopWatch.stop();
        stopWatch.start("snail_task2");
        TimeUnit.SECONDS.sleep(2);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

}
