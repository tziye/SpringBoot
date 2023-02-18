package com.function.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 自定义请求监听器
 */
@Slf4j
@Component
public class MyEventListener {

    /**
     * 异步执行，默认是同步的
     */
    @Async
    @EventListener
    public void onApplicationEvent(MyEvent event) {
        log.info("【MyEvent】：{}", event);
    }
}