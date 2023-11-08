package com.foodiefinder.restaurants.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private long userId;
    private int value;
    private String comment;
}
