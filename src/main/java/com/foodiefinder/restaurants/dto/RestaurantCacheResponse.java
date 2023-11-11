package com.foodiefinder.restaurants.dto;

import lombok.Getter;

@Getter
public class RestaurantCacheResponse {
    private Long id;
    private Double rating;
    private Long reviewCount;
    private String businessPlaceName;
    private String sanitationBusinessCondition;
    private Double distance;

    private RestaurantCacheResponse(Long id, Double rating, Long reviewCount, String businessPlaceName, String sanitationBusinessCondition, Double distance) {
        this.id = id;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.businessPlaceName = businessPlaceName;
        this.sanitationBusinessCondition = sanitationBusinessCondition;
        this.distance = distance;
    }
    public static RestaurantCacheResponse fromCache(Long id, Double rating, Long reviewCount, String businessPlaceName, String sanitationBusinessCondition, Double distance){
        return new RestaurantCacheResponse(id, rating, reviewCount, businessPlaceName,sanitationBusinessCondition ,distance);
    }
}
