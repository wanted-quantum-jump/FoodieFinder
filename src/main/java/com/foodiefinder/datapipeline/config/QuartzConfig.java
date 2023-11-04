package com.foodiefinder.datapipeline.config;

import com.foodiefinder.datapipeline.job.OpenApiJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    @Bean("openApiJobDetailBean")
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(OpenApiJob.class);
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        factoryBean.setJobDataAsMap(new JobDataMap());
        factoryBean.setName("openApiJob");
        factoryBean.setGroup("openApiJobGroup");
        return factoryBean;
    }

    @Bean("openApiCronTriggerBean")
    public Trigger cronTrigger(
            @Qualifier("openApiJobDetailBean")
            JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                // 현재는 테스트로 인해 실행시 한번만 동작되도록 주석 설정
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    @Bean
    public Scheduler scheduler(
            @Qualifier("openApiJobDetailBean")
            JobDetail jobDetail,
            @Qualifier("openApiCronTriggerBean")
            Trigger trigger,
            JobListener openApiJobListener,
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
        scheduler.getListenerManager().addJobListener(
                openApiJobListener,KeyMatcher.keyEquals(new JobKey("openApiJob","openApiJobGroup"))
        );

        return scheduler;
    }
}
