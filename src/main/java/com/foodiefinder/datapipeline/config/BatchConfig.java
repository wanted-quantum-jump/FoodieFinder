package com.foodiefinder.datapipeline.config;

import com.foodiefinder.datapipeline.enums.OpenApiUrl;
import com.foodiefinder.datapipeline.job.StateHandler;
import com.foodiefinder.datapipeline.provider.UrlWithParamsProvider;
import com.foodiefinder.datapipeline.reader.OpenApiPagingItemReader;
import com.foodiefinder.datapipeline.step.ChunkOrientedStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BatchConfig {

    // TODO : itemProcessor, itemWriter 입력
    // TODO : ChunkOrientedStep<String, String -> Repository 에 저장할 클래스로 변경>
    @Bean
    public ChunkOrientedStep<String, String> chunkOrientedStep(OpenApiPagingItemReader<String> openApiPagingItemReader) {
        return ChunkOrientedStep.<String, String>builder()
                .itemReader(openApiPagingItemReader)
//                .itemProcessor()
//                .itemWriter()
                .build();
    }

    @Bean
    public OpenApiPagingItemReader<String> openApiPagingItemReader(StateHandler jobStateHandler) {
        OpenApiPagingItemReader<String> reader = new OpenApiPagingItemReader<>();

        // StateHandler 설정
        reader.setStateHandler(jobStateHandler);

        // Url List 설정
        List<String> urlList = new ArrayList<>();
        OpenApiUrl[] values = OpenApiUrl.values();
        for (OpenApiUrl value : values) {
            urlList.add(value.getUrl());
        }
        reader.setApiUrlList(urlList);

        // HttpRequest 로 불러오는 ItemType 클래스 넘기기
        reader.setItemType(String.class);

        // url 고정 파라미터 설정
        Map<String, String> params = new HashMap<>();
        params.put("KEY", "118b362899f04bcaaa06b7d0cd22c72f");
        params.put("Type", "json");
        reader.setParams(params);

        // 한번에 읽어 올 페이지 사이즈 이름설정
        reader.setPageSize("pSize", 1000);

        // 페이지 인덱스 파라미터 이름, 페이지 시작 인덱스 설정 
        reader.setPage("pIndex", 1);

        // 끝 페이지 임을 알리는 Response 작성
        reader.setEndOfPageResponse("{\"RESULT\":{\"CODE\":\"INFO-200\",\"MESSAGE\":\"해당하는 데이터가 없습니다.\"}}", HttpStatusCode.valueOf(200));

        // UrlProvider 설정
        reader.setUrlProvider(new UrlWithParamsProvider());

        return reader;
    }
}
