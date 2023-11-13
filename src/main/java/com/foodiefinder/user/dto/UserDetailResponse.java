package com.foodiefinder.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDetailResponse {

    private Long id;
    private String account;
    private String latitude;
    private String longitude;

    @Builder
    public UserDetailResponse(Long id, String account, String latitude, String longitude) {
        this.id = id;
        this.account = account;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
