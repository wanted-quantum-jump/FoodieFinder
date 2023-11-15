package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.util.hash.HashGenerator;
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

    public boolean hasResponseCache(String response) {
        byte[] responseHash = computeHashForResponse(response);
        try(RedisConnection connection = cacheUtils.getConnection()) {
            return connection.stringCommands().get(responseHash) != null;
        }
    }

    public void inputResponseCache(String response) {
        log.info("Response 해시, 캐싱 시작");
        byte[] responseHash = computeHashForResponse(response);

        try (RedisConnection connection = cacheUtils.getConnection()) {
            connection.stringCommands()
                    .set(responseHash, "true".getBytes(),
                            Expiration.seconds(RESPONSE_EXPIRE_TIME),
                            RedisStringCommands.SetOption.UPSERT);

        }
        log.info("Response 해시, 캐싱 완료");
    }

    private byte[] computeHashForResponse(String response) {
        return (CacheKeyPrefix.DATAPIPELINE_RESPONSE.getKeyPrefix() +
                HashGenerator.calculateSHA256(response)).getBytes();
    }

    public void executeModifyExpire(String response) {
        byte[] responseHash = computeHashForResponse(response);

        try(RedisConnection connection = cacheUtils.getConnection()) {
            connection.keyCommands()
                    .expire(responseHash,
                            RESPONSE_EXPIRE_TIME
                    );
        }
    }
}