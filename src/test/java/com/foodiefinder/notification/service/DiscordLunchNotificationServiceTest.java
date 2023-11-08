package com.foodiefinder.notification.service;

import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.settings.entity.NotificationSetting;
import com.foodiefinder.settings.repository.NotificationSettingRepository;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("단위테스트 - DiscordLunchNotificationService")
class DiscordLunchNotificationServiceTest {

    final Long MOCK_USER_ID = 1L;
    User MOCK_USER = new User("testaccount123", "password123");
    NotificationSetting NOTIFICATION_SETTING = new NotificationSetting(MOCK_USER,
            Boolean.TRUE, "중국식",
            "https://discord.com/api/webhooks/1171008904805744681/bwClio2PWCUT4wL7A-fSqloOcmMFNvPC4Giq4gqWpRFmk3lWS8Uaxx1bGRnfUZ1TJc4u",
            1000);
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private DiscordLunchNotificationService discordLunchNotificationService;
    @Mock
    private NotificationSettingRepository notificationSettingRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        //MOCK_USER 설정
        ReflectionTestUtils.setField(MOCK_USER, "id", MOCK_USER_ID);
        ReflectionTestUtils.setField(MOCK_USER, "latitude", "35.1234");
        ReflectionTestUtils.setField(MOCK_USER, "longitude", "126.1234");
    }


    @DisplayName("검증 통과 : 유효한 NotificationSetting")
    @Test
    void testIsNotificationSettingValid() {
        //when
        boolean result = ReflectionTestUtils.invokeMethod(
                DiscordLunchNotificationService.class, "isInvalidNotificationSetting", new HashSet<>(),
                NOTIFICATION_SETTING);
        //than
        Assertions.assertThat(result).isFalse(); // valid함
    }

    @DisplayName("검증 실패 : WebHook Url이 null")
    @Test
    void testIsNotificationSettingValid_NoWebhookUrl() {
        //when
        ReflectionTestUtils.setField(NOTIFICATION_SETTING, "webHookUrl", null); //Webhook url이 null
        //than
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                DiscordLunchNotificationService.class, "isInvalidNotificationSetting", new HashSet<>(),
                NOTIFICATION_SETTING))
                .isInstanceOf(NullPointerException.class); //예외가 발생해야함

    }

    @DisplayName("검증 실패 : WebHook Url은 blank이면 안됨")
    @Test
    void testIsNotificationSettingValid_BlankWebhookUrl() {
        //when
        ReflectionTestUtils.setField(NOTIFICATION_SETTING, "webHookUrl", ""); //Webhook url이 null
        //than
        boolean result = ReflectionTestUtils.invokeMethod(
                DiscordLunchNotificationService.class, "isInvalidNotificationSetting", new HashSet<>(),
                NOTIFICATION_SETTING);//예외는 발생하지 않고 skip

        Assertions.assertThat(result).isTrue(); //valid하지 않음
    }

    @DisplayName("검증 실패 : recommendationCategories가 null")
    @Test
    void testIsNotificationSettingValid_NoCategory() {
        //when
        ReflectionTestUtils.setField(NOTIFICATION_SETTING, "recommendationCategories",
                null); //recommendationCategories가 null
        //than
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                DiscordLunchNotificationService.class, "isInvalidNotificationSetting", new HashSet<>(),
                NOTIFICATION_SETTING))
                .isInstanceOf(NullPointerException.class); //예외가 발생해야함
    }

    @DisplayName("검증 실패 : 유저의 위치 정보 없음 ")
    @Test
    void testIsNotificationSettingValid_NoLocation() {
        //when :유저 위치정보 null
        ReflectionTestUtils.setField(MOCK_USER, "latitude", null);
        ReflectionTestUtils.setField(MOCK_USER, "longitude", null);

        //than
        boolean result = ReflectionTestUtils.invokeMethod(
                DiscordLunchNotificationService.class, "isInvalidNotificationSetting", new HashSet<>(),
                NOTIFICATION_SETTING);//예외는 발생하지 않고 skip

        Assertions.assertThat(result).isTrue(); //valid하지 않음
    }


}