package com.foodiefinder.settings.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.notification.service.DiscordLunchNotificationService;
import com.foodiefinder.settings.dto.ChangeNotificationSettingRequest;
import com.foodiefinder.settings.service.NotificationSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("단위테스트 - NotificationSettingController")
@Transactional
@ExtendWith(MockitoExtension.class)
class NotificationSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationSettingController notificationSettingController;

    @Mock
    private NotificationSettingService notificationSettingService;

    @Mock
    private DiscordLunchNotificationService discordLunchNotificationService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(notificationSettingController).build();
    }

    @Test
    @DisplayName("성공 - 상태코드 204")
    public void testChangeNotificationSetting() throws Exception {
        // given
        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(1L);
        request.setRecommendationRange(1000);
        request.setRecommendationCategories("일식, 중국식");
        request.setWebHookUrl(
            "https://discord.com/api/webhooks/1171008904805744681/bwClio2PWCUT4wL7A-fSqloOcmMFNvPC4Giq4gqWpRFmk3lWS8Uaxx1bGRnfUZ1TJc4u");
        request.setIsLunchRecommendationAllowed(Boolean.TRUE);

        // when
        doNothing().when(notificationSettingService)
            .change(any(ChangeNotificationSettingRequest.class));

        //than
        mockMvc.perform(MockMvcRequestBuilders.post("/api/settings/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("실패 - 상태코드 4xx (추천 카테고리 null)")
    public void testErrorWithNoRecommendationCategories() throws Exception {
        // given
        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(1L);
        request.setRecommendationRange(1000);
        request.setRecommendationCategories(null); //null!
        request.setWebHookUrl(
            "https://discord.com/api/webhooks/1171008904805744681/bwClio2PWCUT4wL7A-fSqloOcmMFNvPC4Giq4gqWpRFmk3lWS8Uaxx1bGRnfUZ1TJc4u");
        request.setIsLunchRecommendationAllowed(Boolean.TRUE);

        // when

        //than
        mockMvc.perform(MockMvcRequestBuilders.post("/api/settings/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    @DisplayName("실패 - 상태코드 4xx (추천 카테고리는 유효한 값을 1개 이상 포함해야함)")
    public void testErrorWithEmptyRecommendationCategories() throws Exception {
        // given
        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(1L);
        request.setRecommendationRange(1000);
        request.setRecommendationCategories("읽싥, 중국씩"); //유효하지 않음
        request.setWebHookUrl(
            "https://discord.com/api/webhooks/1171008904805744681/bwClio2PWCUT4wL7A-fSqloOcmMFNvPC4Giq4gqWpRFmk3lWS8Uaxx1bGRnfUZ1TJc4u");
        request.setIsLunchRecommendationAllowed(Boolean.TRUE);

        //than
        mockMvc.perform(MockMvcRequestBuilders.post("/api/settings/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    @DisplayName("실패 - 상태코드 4xx (WebHookUrl는 null 비허용)")
    public void testErrorWithNullWebHookUrl() throws Exception {
        // given
        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(1L);
        request.setRecommendationRange(1000);
        request.setRecommendationCategories("일식");
        request.setWebHookUrl(null);//NotBlank여야함
        request.setIsLunchRecommendationAllowed(Boolean.TRUE);

        //than
        mockMvc.perform(MockMvcRequestBuilders.post("/api/settings/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }

    @Test
    @DisplayName("실패 - 상태코드 4xx (WebHookUrl는 blank 비허용)")
    public void testErrorWithEmptyWebHookUrl() throws Exception {
        // given
        ChangeNotificationSettingRequest request = new ChangeNotificationSettingRequest();
        request.setUserId(1L);
        request.setRecommendationRange(1000);
        request.setRecommendationCategories("일식");
        request.setWebHookUrl("");//NotBlank여야함
        request.setIsLunchRecommendationAllowed(Boolean.TRUE);

        //than
        mockMvc.perform(MockMvcRequestBuilders.post("/api/settings/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());

    }


}