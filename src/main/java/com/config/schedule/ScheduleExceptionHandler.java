package com.config.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * 定时任务异常处理器
 */
@Slf4j
@Component
public class ScheduleExceptionHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("定时任务出现异常", t);
    }

}
