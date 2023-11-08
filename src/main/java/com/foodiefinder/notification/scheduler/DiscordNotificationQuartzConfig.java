package com.foodiefinder.notification.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


@Configuration
@RequiredArgsConstructor
public class DiscordNotificationQuartzConfig {

    final String EVERY_NOON = "0 0 12 * * ?";

    @Bean
    public JobDetail discordNotificationJobDetail() {
        return JobBuilder.newJob().ofType(DiscordNotificationJob.class)
            .storeDurably()
            .withIdentity("discordNotificationJobDetail")
            .withDescription("Invoke Discord Notification service...")
            .build();
    }


    @Bean
    public Trigger discordNotificationJobTrigger(
        @Qualifier("discordNotificationJobDetail") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
            .withIdentity("discordNotificationJobTrigger")
            .withSchedule(cronSchedule(EVERY_NOON)
                .inTimeZone(TimeZone.getTimeZone("Asia/Seoul")))
            .build();
    }

    @Bean
    public Scheduler discordJobScheduler(
        @Qualifier("discordNotificationJobDetail")
        JobDetail jobDetail,
        @Qualifier("discordNotificationJobTrigger")
        Trigger trigger,
        SchedulerFactoryBean schedulerFactoryBean
    ) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (!scheduler.checkExists(jobDetail.getKey())) {
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            scheduler.addJob(jobDetail, true, true);
            if (!scheduler.checkExists(trigger.getKey())) {
                scheduler.scheduleJob(trigger);
            }
        }
        return scheduler;
    }
}
