package com.controller.quartz;

import com.common.Result;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务管理
 */
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    private static final String JOB1_CRON = "/5 * * * * ? ";
    private static final String JOB2_CRON = "/9 * * * * ? ";
    private static final String GROUP = "g1";
    private Scheduler scheduler;

    {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/start")
    public Result<String> start() throws SchedulerException {
        JobDetail job1 = JobBuilder.newJob(Job1.class).withIdentity("Job1", GROUP).build();
        CronTrigger job1Trigger = TriggerBuilder.newTrigger().withIdentity("Job1Trigger", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(JOB1_CRON)).build();
        scheduler.scheduleJob(job1, job1Trigger);

        JobDetail job2 = JobBuilder.newJob(Job2.class).withIdentity("Job2", GROUP).build();
        CronTrigger job2Trigger = TriggerBuilder.newTrigger().withIdentity("Job2Trigger", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(JOB2_CRON)).build();
        scheduler.scheduleJob(job2, job2Trigger);

        scheduler.start();
        return Result.success();
    }

    @GetMapping("/shutdown")
    public Result<String> shutdown() throws SchedulerException {
        scheduler.shutdown();
        return Result.success();
    }

}