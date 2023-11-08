package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.cache.DataPipelineCacheRepository;
import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CombineRestaurantWriter implements ItemWriter<CombineRestaurantProcessorResultData> {

    private final RestaurantWriter restaurantWriter;
    private final DataPipelineCacheRepository dataPipelineCacheRepository;

    @Override
    public void write(CombineRestaurantProcessorResultData input) {
        if (input != null) {
            if(!input.getRestaurants().isEmpty()) {
                restaurantWriter.write(input.getRestaurants());
                // Restaurant 캐시 데이터 저장
                dataPipelineCacheRepository.setRestaurantCache(input.getRestaurants());
            }
            // input 이 null 은 아니지만 response body 만 변경되었으며 restaurant 는 변경이 안된 경우 response 만 캐시에 저장.
            dataPipelineCacheRepository.setResponseCache(input.getResponse());
        }
    }
}
