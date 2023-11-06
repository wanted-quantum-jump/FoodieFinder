package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CombineRestaurantWriter implements ItemWriter<CombineRestaurantProcessorResultData> {

    private final RestaurantWriter restaurantWriter;

    @Override
    public void write(CombineRestaurantProcessorResultData input) {
        restaurantWriter.write(input.getRestaurants());
    }
}
