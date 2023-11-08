package com.foodiefinder.settings.service;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.notification.repository.NotificationSettingRepository;
import com.foodiefinder.settings.dto.ChangeNotificationSettingRequest;
import com.foodiefinder.settings.entity.NotificationSetting;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;

    @Transactional
    public void change(@Valid ChangeNotificationSettingRequest changeRequest) {
        Long userId = changeRequest.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        createOrUpdate(changeRequest, userId, user);

    }

    private void createOrUpdate(ChangeNotificationSettingRequest changeRequest, Long userId, User user) {

        NotificationSetting changedSetting = notificationSettingRepository.findByUserId(userId).orElseGet(() ->
                new NotificationSetting(user)
        );

        changedSetting.update(changeRequest.getIsLunchRecommendationAllowed(), changeRequest.getRecommendationCategories(), changeRequest.getWebHookUrl(), changeRequest.getRecommendationRange());
        notificationSettingRepository.save(changedSetting);

    }
}
