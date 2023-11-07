package com.foodiefinder.notification.service;


import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.notification.dto.DiscordMessageDto;
import com.foodiefinder.notification.dto.Message;
import com.foodiefinder.notification.repository.NotificationSettingRepository;
import com.foodiefinder.settings.entity.NotificationSetting;
import com.foodiefinder.user.entity.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordLunchNotificationService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final RestaurantRepository restaurantRepository;

    public void sendMessages() {
        // 1.알림 발송 대상 : 추천을 원하는 유저
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByIsLunchRecommendationAllowed(
            Boolean.TRUE);
        // 2.알림 발송 대상을 위한 메시지 생성
        List<Message> messages = createMessages(notificationSettings);
        // 3.메시지 일괄 발송
        sendAllMessagesUsing(messages);
    }

    /***
     *  2.알림 발송 대상을 위한 메시지 생성
     */
    private List<Message> createMessages(List<NotificationSetting> notificationSettings) {
        List<Message> notifications = new ArrayList<>();
        Set<String> webhookUrls = new HashSet<>(); //중복 url 제거를 위한 set
        for (NotificationSetting ns : notificationSettings) {
            if (IsNotificationSettingValid(webhookUrls, ns)) {
                continue;
            }
            webhookUrls.add(ns.getWebHookUrl());
            notifications.add(new Message(ns.getWebHookUrl(), createDiscordMessageDTO(ns)));
        }
        return notifications;
    }

    /***
     * 유저 설정이 잘못되었는지 검증 , 잘못되었으면 ture 리턴
     */
    private static boolean IsNotificationSettingValid(Set<String> webhookUrls,
        NotificationSetting ns) {
        if (ns.hasNoWebhookUrl()) {
            log.info("[메시지 발송 실패] 유저 {}의 webhookUrl이 없습니다.", ns.getUser().getAccount());
            return true;
        }
        if (webhookUrls.contains(ns.getWebHookUrl())) {
            log.info("[메시지 발송 실패] 유저 {}의 webhookUrl이 다른 유저의 것과 중복되어 무시됩니다.",
                ns.getUser().getAccount());
            return true;
        }
        if (ns.isUserLocationNotExist()) {
            log.info("[메시지 발송 실패] 유저 {}의 위치 정보가 확인되지 않아 메시지 발송이 실패했습니다.",
                ns.getUser().getAccount());
            return true;
        }
        if (ns.hasNotValidRecommendationCategories()) {
            log.info("[메시지 발송 실패] 유저 {}의 추천 카테고리가 0개입니다. ({})", ns.getUser().getAccount(),
                ns.getRecommendationCategories());
            return true;
        }
        return false;
    }

    /**
     * 3.메시지 일괄 발송
     */
    private static void sendAllMessagesUsing(List<Message> messageList) {
        //메시지 전송
        for (Message message : messageList) {
            WebClient webClient = WebClient
                .builder()
                .baseUrl(message.getWebhookUrl()) //디스코드 웹훅 url
                .build();

            WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClient
                .post()//post
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message.getData());// 메시지는 body에 추가

            requestHeadersSpec.retrieve().bodyToMono(String.class).subscribe();//  non block
        }
    }

    /***
     * 2.1 유저별 맞춤 메시지 생성
     */
    private DiscordMessageDto createDiscordMessageDTO(NotificationSetting notificationSetting) {
        User targetUser = notificationSetting.getUser();

        Double lat = Double.parseDouble(targetUser.getLatitude());
        Double lon = Double.parseDouble(targetUser.getLongitude());
        Integer range = notificationSetting.getRecommendationRange(); //meter
        //2.1.1 유저 반경 distance 미터 내의 모든 식당 조회
        List<Restaurant> allRestaurantsInRange = findAllRestaurantsInRange(lat, lon, range);
        //2.1.2 조회한 식당 정보, 유저 설정 기반으로 메시지 내용 생성
        return createMessageContent(notificationSetting, lat, lon, range, allRestaurantsInRange,
            targetUser);
    }

    /***
     * 2.1.1 유저의 (lat, lon)을 중심으로 하는, 한 변의 길이가 distance *2 인 정사각형의 bottomleft, topRight 가져온다. <br>
     * DB에서 데이터 가져올 때 범위 줄이기 위해 사용
     */

    private List<Restaurant> findAllRestaurantsInRange(Double lat, Double lon, Integer range) {
        GeoUtils.Coordinates bottomLeft = GeoUtils.calculateBottomLeftCoordinate(lat, lon, range);
        GeoUtils.Coordinates TopRight = GeoUtils.calculateTopRightCoordinate(lat, lon, range);
        return restaurantRepository.findByLatitudeBetweenAndLongitudeBetweenOrderByAverageRatingDesc(
            bottomLeft.latitude, TopRight.latitude, bottomLeft.longitude, TopRight.longitude);
    }

    /***
     * 2.1.2 조회한 식당 정보, 유저 설정 기반으로 메시지 내용 생성
     */
    private static DiscordMessageDto createMessageContent(NotificationSetting notificationSetting,
        Double lat, Double lon, Integer range, List<Restaurant> allRestaurantsInRange,
        User targetUser) {
        //DiscordMessage 형식에 맞게 Dto 생성
        DiscordMessageDto discordMessageDto = new DiscordMessageDto();
        String rawCategoriesString = notificationSetting.getRecommendationCategories();
        List<String> selectedCategories = Arrays.stream(rawCategoriesString.split(","))
            .map(String::trim).toList();
        //유저가 선택한 유형의 음식점만 결과 메시지에 추가
        for (String category : selectedCategories) {
            discordMessageDto.addField(category, allRestaurantsInRange, lat, lon, range);
        }
        discordMessageDto.createContent(
            selectedCategories.get(new Random().nextInt(selectedCategories.size())),
            targetUser.getAccount(), range);
        return discordMessageDto;
    }

}
