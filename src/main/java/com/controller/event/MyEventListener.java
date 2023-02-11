package com.controller.event;

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

    @Async  // 异步执行，否则是同步的
    @EventListener
    public void onApplicationEvent(MyEvent event) {
        log.info("事件监听器，Thread：{}，消息：{}", Thread.currentThread().getName(), event);
    }
}