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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataPipelineRestaurantCacheRepository {
    private final CacheUtils cacheUtils;

    /**
     * 가게를 찾기위해 캐시에서 오차 범위를 고려해 10미터 이내의 가게 목록을 받아온다.
     * @param restaurantCacheDtoList
     * @return
     */
    public List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> findGeoResultsListByRestaurantDtoList(List<RestaurantCacheDto> restaurantCacheDtoList) {
        List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList = new ArrayList<>();

        try(RedisConnection connection = cacheUtils.getConnection()) {
            connection.openPipeline();
            restaurantCacheDtoList.stream()
                    .forEach(data ->
                            connection.geoCommands()
                                    .geoRadius(
                                            (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + data.getSigunName()).getBytes(),
                                            cacheUtils.getCircle(data.getLatitude(), data.getLongitude(), 0.01, Metrics.KILOMETERS),
                                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().sortAscending()
                                    )
                    );
            List<Object> closePipelineObjectResultList = connection.closePipeline();

            for (Object objectResult : closePipelineObjectResultList) {
                if (objectResult instanceof GeoResults) {
                    @SuppressWarnings("unchecked")
                    GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults = (GeoResults<RedisGeoCommands.GeoLocation<byte[]>>) objectResult;
                    geoResultsList.add(geoResults);
                }
            }

            return geoResultsList;
        } catch (ClassCastException e){
            log.error("GeoResults 캐스팅에 실패 하였습니다.");
        }

        // 실패 시 빈 리스트 반환
        return Collections.emptyList();
    }

    /**
     * ! restaurantCacheDtoList 와 geoResultsList 는 인덱스가 일치해야 합니다.
     * 캐시와 데이터 베이스의 불일치를 찾아 업데이트 해야할 목록의 RestaurantCacheDto 를 반환합니다.
     * @param geoResultsList
     * @param restaurantCacheDtoList
     * @return
     */
    public List<RestaurantCacheDto> needUpdateRestaurantCacheDtoList(
            List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList,
            List<RestaurantCacheDto> restaurantCacheDtoList) {

        try (RedisConnection connection = cacheUtils.getConnection()) {
            connection.openPipeline();
            List<RestaurantCacheDto> needUpdateRestaurantDtoList = IntStream.range(0, geoResultsList.size())
                    .mapToObj(index -> {
                        GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults = geoResultsList.get(index);

                        if (geoResults.getContent().isEmpty()) {
                            return restaurantCacheDtoList.get(index);
                        }

                        boolean exists = isGeoResultsMatchRestaurant(
                                geoResults, restaurantCacheDtoList.get(index)
                        );

                        if (exists) {
                            return null;
                        }

                        RestaurantCacheDto restaurantCacheDto = restaurantCacheDtoList.get(index);
                        connection.zSetCommands()
                                .zRem(
                                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                                        restaurantCacheDto.toString().getBytes()
                                );
                        return restaurantCacheDtoList.get(index);
                    })
                    .filter(Objects::nonNull)
                    .toList();
            connection.closePipeline();
            return needUpdateRestaurantDtoList;
        }
    }

    /**
     * GeoResults 에 매칭되는 RestaurantCacheDto 가 있는지 확인합니다.
     * @param geoResults
     * @param restaurant
     * @return
     */
    private boolean isGeoResultsMatchRestaurant(
            GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults,
            RestaurantCacheDto restaurant
    ) {
        return geoResults.getContent().stream()
                .anyMatch(
                        data -> {
                            String member = cacheUtils.decodeFromByteArray(data.getContent().getName());
                            return restaurant.toString().equals(member);
                        }
                );
    }

    /**
     * Restaurant 을 캐싱한다.
     * @param restaurantCacheDtoList
     */
    public void inputRestaurantCache(List<RestaurantCacheDto> restaurantCacheDtoList) {
        log.info("Restaurant {} 건 캐싱 시작",restaurantCacheDtoList.size());
        // GEORADIUS 로 같은 위도, 경도 그리고 같은 id 일 때, 리뷰, 카운트가 다르다면 삭제하고 다시 삽입
        try(RedisConnection connection = cacheUtils.getConnection()) {
            connection.openPipeline(); // 분리
            restaurantCacheDtoList.stream()
                    .forEach(data ->
                            connection.geoCommands()
                                    .geoAdd(
                                            (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + data.getSigunName()).getBytes(),
                                            new Point(data.getLongitude(), data.getLatitude()),
                                            data.toString().getBytes()
                                    )
                    );
            connection.closePipeline();
        }
        log.info("Restaurant 캐싱 종료. Restaurant {} 건 갱신 성공", restaurantCacheDtoList.size());
    }
}
