package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.datapipeline.cache.DataPipelineApiResponseCacheRepository;
import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CombineRestaurantProcessor implements ItemProcessor<String, CombineRestaurantProcessorResultData> {

    private final RawRestaurantProcessor rawRestaurantProcessor;
    private final RestaurantProcessor restaurantProcessor;
    private final DataPipelineApiResponseCacheRepository dataPipelineApiResponseCacheRepository;

    @Override
    public CombineRestaurantProcessorResultData process(String item) {
        // response 캐싱 : response 바디 문자열 전문, 응답이 다를경우 저장
        if(!dataPipelineApiResponseCacheRepository.hasResponseCache(item)) {
            List<RawRestaurant> rawRestaurants = rawRestaurantProcessor.process(item);
            List<Restaurant> restaurants = restaurantProcessor.process(rawRestaurants);
            return CombineRestaurantProcessorResultData.of(item, rawRestaurants, restaurants);
        }
        else{
            // 캐시에 있다면, 내용 업데이트
            dataPipelineApiResponseCacheRepository.inputResponseCache(item);
            return null;
        }
    }
}