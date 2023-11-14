package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.util.hash.HashGenerator;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataPipelineApiResponseCacheRepository {
    private final long RESPONSE_EXPIRE_TIME = 5400;
    private final CacheUtils cacheUtils;

    /**
     * 레디스를 통해 해시 값으로 응답값 비교 후, 없으면 false, 존재하면 Expire 갱신
     * @param response 응답 문자열
     * @return 존재 여부
     */
    public boolean hasResponseCache(String response) {
        byte[] responseHash = getHash(response);
        RedisConnection connection = cacheUtils.getConnection();
        boolean isExist = connection.stringCommands().get(responseHash) != null;
        connection.close();
        return isExist;
    }

    /**
     * response 에 대한 해시값을 레디스에 저장
     * @param response
     * @return
     */
    public boolean inputResponseCache(String response) {
        log.info("Response 해시, 캐싱 시작");
        byte[] responseHash = getHash(response);
        RedisConnection connection = cacheUtils.getConnection();
        boolean set = connection.stringCommands()
                .set(responseHash, "true".getBytes(),
                        Expiration.seconds(RESPONSE_EXPIRE_TIME),
                        RedisStringCommands.SetOption.UPSERT);
        connection.close();
        log.info("Response 해시, 캐싱 완료");
        return set;
    }

    /**
     * 해시 값 생성
     * @param response response 의 문자열
     * @return 변환된 해시 값
     */
    private byte[] getHash(String response) {
        return (CacheKeyPrefix.DATAPIPELINE_RESPONSE.getKeyPrefix() +
                HashGenerator.calculateSHA256(response)).getBytes();
    }
}