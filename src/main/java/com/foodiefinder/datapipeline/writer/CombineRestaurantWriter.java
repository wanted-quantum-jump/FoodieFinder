package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CombineRestaurantWriter implements ItemWriter<CombineRestaurantProcessorResultData> {

    private final RestaurantWriter restaurantWriter;
    private final ApiResponseCacheWriter apiResponseCacheWriter;
    private final RestaurantCacheWriter restaurantCacheWriter;
    @Override
    public void write(CombineRestaurantProcessorResultData input) {
        if (input != null) {
            restaurantWriter.write(input.getRestaurants());

            restaurantCacheWriter.write(input.getRestaurants());
            apiResponseCacheWriter.write(input.getResponse());
        }
    }
}
