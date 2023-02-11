package com.controller;

import com.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 任务调度测试入口
 */
@Slf4j
@RestController
@RequestMapping("/schedule")
public class AsyncScheduler {

    @GetMapping("/async")
    public Result<String> async() throws Exception {
        doByAsync();
        log.info("【Async】异步执行已触发");
        return Result.success();
    }

    @Async
    public void doByAsync() throws Exception {
        log.info("【Async】开始执行异步任务：" + new Date());
        Thread.sleep(5_000L);
        log.info("【Async】异步任务执行完毕：" + new Date());
    }

    /**
     * 定时执行，初始化后5秒执行，结束后再隔100秒执行
     */
    @Scheduled(initialDelay = 5_000L, fixedDelay = 100_000L)
    public void doBySchedule() throws Exception {
        log.info("【Scheduled】开始执行定时任务：" + new Date());
        Thread.sleep(10000);
        log.info("【Scheduled】结束执行定时任务：" + new Date());
    }

}
