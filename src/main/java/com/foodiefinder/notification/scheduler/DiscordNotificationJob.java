package com.foodiefinder.notification.scheduler;

import com.foodiefinder.notification.service.DiscordLunchNotificationService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DiscordNotificationJob implements Job {

    private final DiscordLunchNotificationService discordLunchNotificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("점심 메뉴 추천 메시지 발송 시작 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        discordLunchNotificationService.sendMessages();
        log.info("점심 메뉴 추천 메시지 발송 완료 - {}", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

    }
}


