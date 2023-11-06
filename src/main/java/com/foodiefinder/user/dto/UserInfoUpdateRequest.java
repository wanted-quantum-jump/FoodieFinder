package com.foodiefinder.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoUpdateRequest {

    private String latitude;
    private String longitude;
    private boolean lunchRecommendationEnabled;

    @Builder
    public UserInfoUpdateRequest(String latitude, String longitude, boolean lunchRecommendationEnabled) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lunchRecommendationEnabled = lunchRecommendationEnabled;
    }
}
