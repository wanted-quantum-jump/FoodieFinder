package com.foodiefinder.user.entity;

import com.foodiefinder.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false)
    private String password;

    //위도
    private String latitude;

    //경도
    private String longitude;

    //점심추천기능 사용 여부
    private boolean lunchRecommendationEnabled;

    @Builder
    public User(String account, String password) {
        this.account = account;
        this.password = password;
    }


}
