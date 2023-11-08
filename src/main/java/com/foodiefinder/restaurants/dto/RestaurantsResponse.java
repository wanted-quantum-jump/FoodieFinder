package com.foodiefinder.restaurants.dto;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import java.util.List;
import lombok.Data;

@Data
public class RestaurantsResponse {
    private List<Restaurant> restaurants;
    private RestaurantsResponse(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
    public static RestaurantsResponse from(List<Restaurant> restaurants) {
        return new RestaurantsResponse(restaurants);
    }
}