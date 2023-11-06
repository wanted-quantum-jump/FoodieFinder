package com.foodiefinder.user.dto;

import lombok.Getter;

@Getter
public class UserInfoUpdateRequest {

    private String latitude;
    private String longitude;
    private boolean lunchRecommendationEnabled;
}
