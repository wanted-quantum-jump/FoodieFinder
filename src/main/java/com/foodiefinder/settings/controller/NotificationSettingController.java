package com.foodiefinder.settings.controller;


import com.foodiefinder.notification.service.DiscordLunchNotificationService;
import com.foodiefinder.settings.dto.ChangeNotificationSettingRequest;
import com.foodiefinder.settings.service.NotificationSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/notification")
public class NotificationSettingController {


    private final NotificationSettingService notificationSettingService;

    private final DiscordLunchNotificationService discordLunchNotificationService;

    @PostMapping
    public ResponseEntity<?> changeNotificationSetting(@Valid @RequestBody ChangeNotificationSettingRequest notificationSettingRequest) {
        //TODO : 임시로 request 내의 유저  ID 그대로 사용하게 해둠. Spring Security 적용 이후 Authentication 로 처리할 것.
        notificationSettingService.change(notificationSettingRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    @GetMapping
    public ResponseEntity<?> sendAllMessages() {
        //FIXME : 메시지 전송 테스트를 위해 임시로 생성한 메시지 전송 메서드, 스케쥴러 등록 후 제거할 것
        discordLunchNotificationService.sendMessages();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
