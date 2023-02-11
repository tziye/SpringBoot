package com.extension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动后执行，order越小越早执行
 */
@Slf4j
@Component
@Order(1)
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("【应用启动】扩展点：SpringApplication.callRunners()");
    }
}
