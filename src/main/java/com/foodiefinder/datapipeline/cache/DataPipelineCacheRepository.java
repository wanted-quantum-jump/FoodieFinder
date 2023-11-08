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
    private final String RESTAURANT_KEY_PREFIX = "datapipeline:restaurant:";
    private final long EXPIRE_TIME = 5400;
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
                            Expiration.seconds(5400),
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
                        Expiration.seconds(EXPIRE_TIME),
                        RedisStringCommands.SetOption.UPSERT);
        connection.close();
        return set;
    }

    /**
     * Restaurant 가 캐시에 존재하는지 (이전의 데이터가 변했는지 추적하여 변경된 내용이면 저장하기 위해)
     * @param restaurants restaurant
     * @return 변경된 내용의 restaurant
     */
    public List<Restaurant> isRestaurantsCacheExist(List<Restaurant> restaurants) {
        RedisConnection connection = getConnection();
        connection.openPipeline();
        List<byte[]> keys = restaurants.stream()
                .map(restaurant -> (RESTAURANT_KEY_PREFIX + HashGenerator.calculateSHA256(restaurant.toString())).getBytes())
                .collect(Collectors.toList());
        keys.forEach(connection.stringCommands()::get);
        List<Object> cacheResults = connection.closePipeline();
        connection.close();
        List<Restaurant> result = IntStream.range(0, restaurants.size())
                .filter(i -> cacheResults.get(i) == null)
                .mapToObj(restaurants::get)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * restaurant 를 캐시에 저장
     * @param restaurants
     */
    public void setRestaurantCache(List<Restaurant> restaurants) {
        RedisConnection connection = getConnection();
        connection.openPipeline();
        List<byte[]> keys = restaurants.stream()
                .map(restaurant -> (RESTAURANT_KEY_PREFIX + HashGenerator.calculateSHA256(restaurant.toString())).getBytes())
                .collect(Collectors.toList());
        keys.forEach(key -> connection.stringCommands()
                .set(key, "true".getBytes(),
                        Expiration.seconds(EXPIRE_TIME),
                        RedisStringCommands.SetOption.UPSERT)
        );
        connection.closePipeline();
        connection.close();
    }

    private RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }
}