package com.core.util;

import com.common.util.MyUtil;
import com.core.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

@Slf4j
class StopWatchTest extends ApplicationTest {

    @Test
    void testStopWatch() {
        StopWatch stopWatch = new StopWatch("snail");
        stopWatch.start("snail_task1");
        MyUtil.sleep(1);
        stopWatch.stop();
        stopWatch.start("snail_task2");
        MyUtil.sleep(2);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

}
