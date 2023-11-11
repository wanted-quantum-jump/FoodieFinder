package com.foodiefinder.datapipeline.writer.dto;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;

public class RestaurantCacheDto {
    private Long id;
    private Double rating;
    private int reviewCount;
    private String businessPlaceName;
    private String sanitationBusinessCondition;
    private String sigunName;
    private Double latitude;
    private Double longitude;

    private RestaurantCacheDto(Restaurant restaurant, Double rating, int reviewCount) {
        this.id = restaurant.getId();
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.businessPlaceName = restaurant.getBusinessPlaceName();
        this.sanitationBusinessCondition = restaurant.getSanitationBusinessCondition();
        this.sigunName = restaurant.getRoadAddress().split(" ")[0] + ":" + restaurant.getSigunName();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
    }

    public static RestaurantCacheDto setCache(Restaurant restaurant, Double rating, int reviewCount) {
        return new RestaurantCacheDto(restaurant, rating, reviewCount);
    }

    public String toString() {
        return id + ":" + rating + ":" + reviewCount + ":" + businessPlaceName + ":" + sanitationBusinessCondition;
    }

    public Long getId() {
        return id;
    }

    public String getSigunName() {
        return sigunName;
    }

    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
}
