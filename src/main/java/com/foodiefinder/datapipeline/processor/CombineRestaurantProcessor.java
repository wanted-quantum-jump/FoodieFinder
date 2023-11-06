package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CombineRestaurantProcessor implements ItemProcessor<String, CombineRestaurantProcessorResultData>{

    private final RawRestaurantProcessor rawRestaurantProcessor;
    private final RestaurantProcessor restaurantProcessor;

    @Override
    public CombineRestaurantProcessorResultData process(String item) {
        List<RawRestaurant> rawRestaurants = rawRestaurantProcessor.process(item);
        List<Restaurant> restaurants = restaurantProcessor.process(rawRestaurants);
        return CombineRestaurantProcessorResultData.of(rawRestaurants, restaurants);
    }
}