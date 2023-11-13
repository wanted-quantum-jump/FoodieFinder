package com.foodiefinder.settings.controller;


import com.foodiefinder.settings.dto.ChangeNotificationSettingRequest;
import com.foodiefinder.settings.service.NotificationSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/notification")
public class NotificationSettingController {


    private final NotificationSettingService notificationSettingService;

    @PostMapping
    public ResponseEntity<?> changeNotificationSetting(@Valid @RequestBody ChangeNotificationSettingRequest notificationSettingRequest) {
        notificationSettingService.change(notificationSettingRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
