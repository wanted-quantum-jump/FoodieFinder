package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.datapipeline.util.hash.HashGenerator;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class DataPipelineCacheRepository {
    private final String RESPONSE_KEY_PREFIX = "datapipeline:response:";
    private final long RESPONSE_EXPIRE_TIME = 5400;
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 레디스를 통해 해시 값으로 응답값 비교 후, 없으면 false, 존재하면 Expire 갱신
     * @param response 응답 문자열
     * @return 존재 여부
     */
    public boolean isResponseCacheExist(String response) {
        byte[] hash = (RESPONSE_KEY_PREFIX + HashGenerator.calculateSHA256(response)).getBytes();
        RedisConnection connection = getConnection();
        boolean isExist = connection.stringCommands().get(hash) != null;

        if(isExist){
            connection.stringCommands()
                    .set(hash, "true".getBytes(),
                            Expiration.seconds(RESPONSE_EXPIRE_TIME),
                            RedisStringCommands.SetOption.UPSERT);
        }
        connection.close();
        return isExist;
    }

    /**
     * response 에 대한 해시값을 레디스에 저장
     * @param response
     * @return
     */
    public boolean setResponseCache(String response) {
        byte[] hash = (RESPONSE_KEY_PREFIX + HashGenerator.calculateSHA256(response)).getBytes();
        RedisConnection connection = getConnection();
        boolean set = connection.stringCommands()
                .set(hash, "true".getBytes(),
                        Expiration.seconds(RESPONSE_EXPIRE_TIME),
                        RedisStringCommands.SetOption.UPSERT);
        connection.close();
        return set;
    }

    private RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }
}