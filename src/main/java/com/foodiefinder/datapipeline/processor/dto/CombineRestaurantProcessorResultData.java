package com.foodiefinder.datapipeline.processor.dto;

import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;

import java.util.List;

public class CombineRestaurantProcessorResultData {
    private String response;
    private List<RawRestaurant> rawRestaurants;
    private List<Restaurant> restaurants;

    private CombineRestaurantProcessorResultData(String response, List<RawRestaurant> rawRestaurants, List<Restaurant> restaurants){
        this.response = response;
        this.rawRestaurants = rawRestaurants;
        this.restaurants = restaurants;
    }

    public static CombineRestaurantProcessorResultData of(String response, List<RawRestaurant> rawRestaurants, List<Restaurant> restaurants) {
        return new CombineRestaurantProcessorResultData(response, rawRestaurants, restaurants);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
    public String getResponse() {
        return response;
    }
}
