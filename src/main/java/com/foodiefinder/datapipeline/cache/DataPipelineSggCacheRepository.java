package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataPipelineSggCacheRepository {
    private final CacheUtils cacheUtils;
    /**
     * Sgg 데이터 Redis 에 저장, csv 는 "경기", "충청
     * key = map:korea
     * lon, lat
     * member = 지역:시군구
     * @param sggList
     */
    public void inputSggCache(List<Sgg> sggList) {
        log.info("시군구 데이터 캐싱 시작");
        RedisConnection connection = cacheUtils.getConnection();
        connection.openPipeline();
        sggList.forEach(data ->
                        connection.geoCommands()
                                .geoAdd(
                                        CacheKeyPrefix.MAP_KOREA.getKeyPrefix().getBytes(),
                                        new Point(data.getLon(), data.getLat()),
                                        (data.getDosi() + ":" + data.getSgg()).getBytes()
                                ));
        connection.closePipeline();
        connection.close();
        log.info("시군구 데이터 캐싱 종료");
    }

    public List<String> findNearSGG(RedisConnection connection, double latitude, double longitude) {
        // 가까운 지역:시군구 찾기
        GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults = connection.geoCommands()
                .geoRadius(CacheKeyPrefix.MAP_KOREA.getKeyPrefix().getBytes(),
                        cacheUtils.getCircle(latitude, longitude, 500, Metrics.KILOMETERS),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().limit(10).sortAscending()
                );

        if (geoResults == null) {
            return Collections.emptyList();
        }

        return geoResults.getContent().stream()
                .map(geo -> geo.getContent().getName())
                .map(cacheUtils::decodeFromByteArray)
                .toList();
    }
}
