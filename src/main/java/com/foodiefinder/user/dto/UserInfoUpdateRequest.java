package com.foodiefinder.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoUpdateRequest {

    private String latitude;
    private String longitude;

    @Builder
    public UserInfoUpdateRequest(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
