package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.cache.DataPipelineApiResponseCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiResponseCacheWriter implements ItemWriter<String> {

    private final DataPipelineApiResponseCacheRepository dataPipelineApiResponseCacheRepository;

    @Override
    public void write(String input) {
        dataPipelineApiResponseCacheRepository.inputResponseCache(input);
    }
}