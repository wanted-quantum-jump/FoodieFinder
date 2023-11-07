package com.foodiefinder.datapipeline.processor.dto;

import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;

import java.util.List;

public class CombineRestaurantProcessorResultData {
    private List<RawRestaurant> rawRestaurants;
    private List<Restaurant> restaurants;

    private CombineRestaurantProcessorResultData(List<RawRestaurant> rawRestaurants, List<Restaurant> restaurants){
        this.rawRestaurants = rawRestaurants;
        this.restaurants = restaurants;
    }

    public static CombineRestaurantProcessorResultData of(List<RawRestaurant> rawRestaurants, List<Restaurant> restaurants) {
        return new CombineRestaurantProcessorResultData(rawRestaurants, restaurants);
    }

    public List<RawRestaurant> getRawRestaurants() {
        return rawRestaurants;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
