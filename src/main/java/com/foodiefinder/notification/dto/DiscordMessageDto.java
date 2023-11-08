package com.foodiefinder.notification.dto;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.notification.service.GeoUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class DiscordMessageDto {

    //=======================메시지에 포함될 상수 시작=====================

    public static final long MAX_SIZE = 3; // 카테고리 별로 최대 3개의 음식점 추천

    public static final String USERNAME = "Foodie Finder";
    public static final String LUNCH_RECOMMENDATION_MESSAGE = "🍴 오늘 점심은 [%s] 어떠세요? 🍴\n%s님의 %dm근처 맛집 정보 리스트입니다.\n더 다양한 맛집 정보를 원하시면 유저 설정에서 추천 카테고리를 추가해주세요.✨";
    public static final String FIELD_TITLE = "[%s] 추천 맛집 (%d개)";
    public static final String FIELD_NAME = "%s %dm";
    public static final String FOOTER_TEXT = "Enjoy your LunchHere :)";

    //=======================메시지에 포함될 상수 끝=======================

    private String username = USERNAME;
    private String avatar_url = "https://cdn-icons-png.flaticon.com/512/857/857755.png";
    private String content;
    private List<Embed> embeds = new ArrayList<>();


    public void addField(String category, List<Restaurant> restaurants, Double userLat, Double userLon, Integer range) {
        //restaurants는 이미 avgRating순으로 정렬되어 있음
        List<Restaurant> filteredRestaurants = restaurants.stream()
                .filter(restaurant -> restaurant.getSanitationBusinessCondition().equals(category))
                .filter(restaurant -> GeoUtils.calculateDistance(userLat, userLon, restaurant.getLatitude(), restaurant.getLongitude()) <= range) //meter
                .limit(MAX_SIZE)
                .toList();


        // embeed
        Embed embed = new Embed();
        embed.setTitle(String.format(FIELD_TITLE, category, filteredRestaurants.size()));

        for (Restaurant restaurant : filteredRestaurants) {
            Field field = new Field();
            int realDistance = GeoUtils.calculateDistance(userLat, userLon, restaurant.getLatitude(), restaurant.getLongitude());
            field.setName(String.format(FIELD_NAME, restaurant.getBusinessPlaceName(), realDistance));
            field.setValue(restaurant.getRoadAddress());//내용
            embed.fields.add(field);
        }
        this.embeds.add(embed);
    }

    public void createContent(String recommendedCategory, String userAccount, Integer distance) {
        this.content = String.format(LUNCH_RECOMMENDATION_MESSAGE, recommendedCategory, userAccount, distance);
    }

    @Data
    private static class Embed {
        private Author author;
        private String title;
        private String url;
        private String description;
        private int color;
        private List<Field> fields = new ArrayList<>();
        private Thumbnail thumbnail;
        private Image image;
        private Footer footer = new Footer();
    }

    @Data
    private static class Author {
        private String name;
        private String url;
        private String icon_url;
    }

    @Data
    private static class Field {
        private String name;
        private String value;
        private boolean inline;
    }

    @Data
    private static class Thumbnail {
        private String url;
    }

    @Data
    private static class Image {
        private String url;
    }

    @Data
    private static class Footer {
        private String text = FOOTER_TEXT;
        private String icon_url;
    }
}
