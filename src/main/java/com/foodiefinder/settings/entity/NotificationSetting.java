package com.foodiefinder.settings.entity;

import com.foodiefinder.common.entity.BaseTimeEntity;
import com.foodiefinder.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;

    @Column(nullable = false)
    Boolean isLunchRecommendationAllowed = Boolean.FALSE;

    @Column(nullable = false)
    String recommendationCategories;

    @Column(nullable = false)
    String webHookUrl;

    @Column(nullable = false)
    Integer recommendationRange;

    @Builder
    public NotificationSetting(User user, Boolean isLunchRecommendationAllowed,
        String recommendationCategories, String webHookUrl, Integer recommendationRange) {
        this.user = user;
        this.recommendationCategories = recommendationCategories;
        this.isLunchRecommendationAllowed = isLunchRecommendationAllowed;
        this.webHookUrl = webHookUrl;
        this.recommendationRange = recommendationRange;
    }

    // == 생성자 == //
    public NotificationSetting(User user) {
        this.user = user;
    }

    //== 비즈니스 로직 == //
    public void update(Boolean isLunchRecommendationAllowed, String recommendationCategories,
        String webHookUrl, Integer recommendationRange) {
        this.recommendationCategories = recommendationCategories;
        this.isLunchRecommendationAllowed = isLunchRecommendationAllowed;
        this.webHookUrl = webHookUrl;
        this.recommendationRange = recommendationRange;
    }

    // == 검증 로직 ==//
    public boolean hasNotValidRecommendationCategories() {
        return recommendationCategories.isBlank();
    }

    public boolean isUserLocationNotExist() {
        try {
            Double.parseDouble(this.user.getLatitude());
            Double.parseDouble(this.user.getLongitude());
            return false;
        } catch (Exception e) {
            return true; // 위치정보 X
        }
    }

    public boolean hasNoWebhookUrl() {
        return webHookUrl.isBlank();
    }
}
