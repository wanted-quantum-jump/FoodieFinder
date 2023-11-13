package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataPipelineRestaurantCacheRepository {
    private final CacheUtils cacheUtils;
    public void inputDataPipelineRestaurantCache(List<RestaurantCacheDto> restaurantCacheDtoList) {
        log.info("Restaurant {} 건 캐싱 시작",restaurantCacheDtoList.size());
        // GEORADIUS 로 같은 위도, 경도 그리고 같은 id 일 때, 리뷰, 카운트가 다르다면 삭제하고 다시 삽입
        RedisConnection connection = cacheUtils.getConnection();
//        connection.openPipeline();
        List<RestaurantCacheDto> needUpdateRestaurantCacheDtoList = restaurantCacheDtoList.stream()
                .map(data -> {
                    // 같은 위도, 경도의 데이터 검색
                    GeoResults<RedisGeoCommands.GeoLocation<byte[]>> radiusResult = connection.geoCommands()
                            .geoRadius(
                                    (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + data.getSigunName()).getBytes(),
                                    cacheUtils.getCircle(data.getLatitude(), data.getLongitude(), 0.01, Metrics.KILOMETERS),
                                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().sortAscending()
                            );
                    
                    if (radiusResult == null) {
                        return data;
                    }

                    String member = radiusResult.getContent().stream()
                            .map(map -> cacheUtils.decodeFromByteArray(map.getContent().getName()))
                            .filter(filter -> filter.split(":")[0].equals(String.valueOf(data.getId())))
                            .toList().get(0);

                    if (member.equals(data.toString())) {
                        return null;
                    }
                    else {
                        connection.zSetCommands()
                                .zRem(
                                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + data.toString()).getBytes(),
                                        data.toString().getBytes()
                                );
                        return data;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

//        if (needUpdateRestaurantCacheDtoList.isEmpty()) {
//            connection.closePipeline();
//            connection.close();
//            return;
//        }

        connection.openPipeline();
        needUpdateRestaurantCacheDtoList.stream()
                .forEach(data ->
                        connection.geoCommands()
                                .geoAdd(
                                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + data.getSigunName()).getBytes(),
                                        new Point(data.getLongitude(), data.getLatitude()),
                                        data.toString().getBytes()
                                )
                );
        connection.closePipeline();
        connection.close();
        log.info("Restaurant 캐싱 종료. Restaurant {} 건 갱신 성공",needUpdateRestaurantCacheDtoList.size());
    }
}
