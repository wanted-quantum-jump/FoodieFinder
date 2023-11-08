package com.foodiefinder.restaurants.dto;

import lombok.Data;

@Data
public class RatingDto {
    private Long userId;
    private int value;
    private String comment;

    public RatingDto(Long userId, int value, String comment) {
        this.userId = userId;
        this.value = value;
        this.comment = comment;
    }
}
