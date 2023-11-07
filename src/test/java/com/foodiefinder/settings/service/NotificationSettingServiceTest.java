package com.foodiefinder.settings.service;

import com.foodiefinder.notification.repository.NotificationSettingRepository;
import com.foodiefinder.settings.dto.ChangeNotificationSettingRequest;
import com.foodiefinder.settings.entity.NotificationSetting;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.doAnswer;


@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("단위테스트 - NotificationSettingService")
public class NotificationSettingServiceTest {

    final User MOCK_USER = new User("account123", "password123");
    final Long MOCK_USER_ID = 1L;
    final NotificationSetting SETTING = new NotificationSetting(MOCK_USER, Boolean.TRUE, "중국식", "fSqloOcmMFNvPC4Giq4gqWpRFmk3lWS8Uaxx1bGRnfUZ1TJc4u", 1000);

    @InjectMocks
    NotificationSettingService notificationSettingService;
    @Mock
    NotificationSettingRepository notificationSettingRepository;
    @Mock
    UserRepository userRepository;

    @BeforeEach
    void init() {
        doAnswer(invocation -> Optional.of(MOCK_USER)).when(userRepository).findById(MOCK_USER_ID);
        doAnswer(invocation -> Optional.of(SETTING)).when(notificationSettingRepository).findByUserId(MOCK_USER_ID);
    }

    @Test
    @DisplayName("유저 알림 설정 변경 (Request 가 결과에 반영되어야한다.)")
    void change() {

        //given
        String NEW_CATEGORIES = "이동조리";
        Integer NEW_RANGE = 600;
        String NEW_HOOK_URL = "https://discord.com/api/webhooks/test";
        Boolean NEW_ALLOWED = Boolean.FALSE;

        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(MOCK_USER_ID);
        request.setRecommendationRange(NEW_RANGE); //갱신
        request.setRecommendationCategories(NEW_CATEGORIES); // 갱신
        request.setWebHookUrl(NEW_HOOK_URL); //갱신
        request.setIsLunchRecommendationAllowed(NEW_ALLOWED); //생신

        //when
        notificationSettingService.change(request);

        //than
        Assertions.assertThat(notificationSettingRepository.findByUserId(MOCK_USER_ID).get().getRecommendationCategories()).isEqualTo(NEW_CATEGORIES);
        Assertions.assertThat(notificationSettingRepository.findByUserId(MOCK_USER_ID).get().getRecommendationRange()).isEqualTo(NEW_RANGE);
        Assertions.assertThat(notificationSettingRepository.findByUserId(MOCK_USER_ID).get().getWebHookUrl()).isEqualTo(NEW_HOOK_URL);
        Assertions.assertThat(notificationSettingRepository.findByUserId(MOCK_USER_ID).get().getIsLunchRecommendationAllowed()).isEqualTo(NEW_ALLOWED);
    }
}