package com.controller.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

@Slf4j
public class Job1 implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("【Quartz】job1执行了：{}", new Date());
    }
}