package com.foodiefinder.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheUtils {
    private final RedisConnectionFactory redisConnectionFactory;
    public RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    public Circle getCircle(double latitude, double longitude, double range, Metrics metrics) {
        return new Circle(new Point(longitude, latitude), new Distance(range, metrics));
    }
    public String decodeFromByteArray(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * GeoRadius 명령어등 등 GeoResults 를 리턴하는 명령을 파이프라인으로 여러번 사용 후 받은 결과에 대해서만 사용할 것
     */
    public List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> toGeoResultsList(List<Object> objectList) {
        List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList = new ArrayList<>();

        for (Object objectResult : objectList) {
            if (objectResult instanceof GeoResults) {
                @SuppressWarnings("unchecked")
                GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults = (GeoResults<RedisGeoCommands.GeoLocation<byte[]>>) objectResult;
                geoResultsList.add(geoResults);
            }
        }
        return geoResultsList;
    }
}
