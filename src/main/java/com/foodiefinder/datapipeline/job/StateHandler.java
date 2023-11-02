package com.foodiefinder.datapipeline.job;

import java.util.Optional;

/**
 * Job 에서 Step 를 실행하여, 배치 작업을 실행 도중
 * 에러가 발생한다면 처음부터가 아닌 중단된 부분부터 재시도를 N번 해보아야한다.
 * 이를 위해 작업 중간 중간 상태를 저장하고, 다시 실행시 재시도 실행인지 파악해야한다.
 */
public interface StateHandler {
    <T> void saveState(String key, T value);
    <T> Optional<T> loadState(String key, Class<T> clazz);
    void deleteState(String key);
}