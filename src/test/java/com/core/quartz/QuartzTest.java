package com.core.quartz;

import com.common.util.MyUtil;
import com.core.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@Slf4j
class QuartzTest extends ApplicationTest {

    @Test
    void test() throws Exception {
        String group = "g1";
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job1 = JobBuilder.newJob(Job1.class).withIdentity("Job1", group).build();
        CronTrigger job1Trigger = TriggerBuilder.newTrigger().withIdentity("Job1Trigger", group)
                .withSchedule(CronScheduleBuilder.cronSchedule("/2 * * * * ? ")).build();
        scheduler.scheduleJob(job1, job1Trigger);

        JobDetail job2 = JobBuilder.newJob(Job2.class).withIdentity("Job2", group).build();
        CronTrigger job2Trigger = TriggerBuilder.newTrigger().withIdentity("Job2Trigger", group)
                .withSchedule(CronScheduleBuilder.cronSchedule("/3 * * * * ? ")).build();
        scheduler.scheduleJob(job2, job2Trigger);

        scheduler.start();

        MyUtil.sleep(10);
        scheduler.shutdown();
    }

}
