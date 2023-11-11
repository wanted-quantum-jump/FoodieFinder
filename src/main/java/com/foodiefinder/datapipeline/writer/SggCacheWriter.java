package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.cache.DataPipelineSggCacheRepository;
import com.foodiefinder.datapipeline.processor.SggProcessor;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SggCacheWriter implements ItemWriter<String>{
    private final SggProcessor sggProcessor;
    private final DataPipelineSggCacheRepository dataPipelineSggCacheRepository;

    @PostConstruct
    public void init() {
        // 애플리케이션 실행시 최초 1회 실행
        log.info("CSV 캐싱을 시작합니다.");
        write("sgg_lat_lon.csv");
        log.info("CSV 캐싱 완료되었습니다.");
    }

    @Override
    public void write(String input) {
        List<Sgg> sggList = sggProcessor.process("sgg_lat_lon.csv");
        dataPipelineSggCacheRepository.inputSggCache(sggList);
    }
}
