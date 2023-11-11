package com.foodiefinder.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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
}
