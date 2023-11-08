package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.datapipeline.cache.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

import java.util.Base64;

@Repository
@RequiredArgsConstructor
public class DataPipelineCacheRepository {
    private final String RESPONSE_KEY_PREFIX = "datapipeline:response:";
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 레디스를 통해 해시 값으로 응답값 비교 후, 없으면 false, 존재하면 Expire 갱신
     * @param response 응답 문자열
     * @return 존재 여부
     */
    public boolean isResponseCacheExist(String response) {
        byte[] hash = (RESPONSE_KEY_PREFIX + HashGenerator.calculateSHA256(response)).getBytes();
        RedisConnection connection = redisConnectionFactory.getConnection();
        boolean isExist = connection.stringCommands().get(hash) != null;

        if(isExist){
            connection.stringCommands()
                    .set(hash, "true".getBytes(),
                            Expiration.seconds(5400),
                            RedisStringCommands.SetOption.UPSERT);
        }
        connection.close();
        return isExist;
    }

    public boolean setResponseCache(String response) {
        byte[] hash = (RESPONSE_KEY_PREFIX + HashGenerator.calculateSHA256(response)).getBytes();
        RedisConnection connection = redisConnectionFactory.getConnection();
        boolean set = connection.stringCommands()
                .set(hash, "true".getBytes(),
                        Expiration.seconds(5400),
                        RedisStringCommands.SetOption.UPSERT);
        connection.close();
        return set;
    }
}