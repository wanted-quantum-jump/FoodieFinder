package com.foodiefinder.datapipeline.job;

import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Job 의 실행 상태를 추적
 * TODO : 스레드 세이프하게 만들기
 */
@Component
public class JobStateHandler implements StateHandler {
    private JobDataMap jobDataMap = new JobDataMap();

    @Override
    public <T> void saveState(String key, T value) {
        jobDataMap.put(key, value);
    }

    @Override
    public <T> Optional<T> loadState(String key, Class<T> clazz) {
        Object value = jobDataMap.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        if (clazz.isInstance(value)) {
            return Optional.of(clazz.cast(value));
        } else {
            // 값은 있지만 타입이 다를경우, 후에 로깅처리
            return Optional.empty();
        }
    }

    @Override
    public void deleteState(String key) {
        jobDataMap.remove(key);
    }
}