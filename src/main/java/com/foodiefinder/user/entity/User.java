package com.foodiefinder.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;

    private String password;

    //위도
    private String latitude;

    //경도
    private String longitude;

    //점심추천기능 허용 여부
    private boolean lunchRecommendationEnabled;

    @Builder
    public User(String account, String password) {
        this.account = account;
        this.password = password;
    }

    //사용자 설정 업데이트
    public void settingUpdate(String latitude, String longitude, boolean lunchRecommendationEnabled) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lunchRecommendationEnabled = lunchRecommendationEnabled;
    }
}
