package com.foodiefinder.datapipeline.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
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


    public List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> findGeoResultsListByRestaurantDtoList(List<RestaurantCacheDto> restaurantCacheDtoList) {
        try(RedisConnection connection = cacheUtils.getConnection()) {

            List<Object> closePipelineObjectResultList = executeGeoRadiusPipelineByRestaurantCacheDtoList(connection, restaurantCacheDtoList);

            return toGeoResultsList(closePipelineObjectResultList);
        } catch (ClassCastException e){
            log.error("GeoResults 캐스팅에 실패 하였습니다.");
        }

        // 실패 시 빈 리스트 반환
        return Collections.emptyList();
    }

    private List<Object> executeGeoRadiusPipelineByRestaurantCacheDtoList(RedisConnection connection, List<RestaurantCacheDto> restaurantCacheDtoList) {
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
        return connection.closePipeline();
    }

    /**
     * GeoRadius 명령어등 등 GeoResults 를 리턴하는 명령을 파이프라인으로 여러번 사용 후 받은 결과에 대해서만 사용할 것
     */
    private List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> toGeoResultsList(List<Object> objectList) {
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




    public List<RestaurantCacheDto> needModifyRestaurantCacheDtoList(
            List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList,
            List<RestaurantCacheDto> restaurantCacheDtoList) {
        try (RedisConnection connection = cacheUtils.getConnection()) {

            List<RestaurantCacheDto> needDeleteForUpdateRestaurantCacheDtoList = new ArrayList<>();
            List<RestaurantCacheDto> needAddRestaurantDtoList = IntStream.range(0, geoResultsList.size())
                    .mapToObj(index ->
                            new ImmutablePair<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>,RestaurantCacheDto>
                                    (geoResultsList.get(index),restaurantCacheDtoList.get(index))
                    )
                    .map(pair -> {
                        if (isGeoResultsEmpty(pair.getLeft())) {
                            return pair.getRight();
                        }

                        if (!isAnyGeoResultsContentMatchRestaurantCacheDto(
                                pair.getLeft(), pair.getRight())) {
                            needDeleteForUpdateRestaurantCacheDtoList.add(pair.getRight());
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            executeZRemPipelineByRestaurantCacheDtoList(connection, needDeleteForUpdateRestaurantCacheDtoList);
            needAddRestaurantDtoList.addAll(needDeleteForUpdateRestaurantCacheDtoList);
            return needAddRestaurantDtoList;
        }
    }

    private boolean isGeoResultsEmpty(GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults) {
        return geoResults.getContent().isEmpty();
    }

    private boolean isAnyGeoResultsContentMatchRestaurantCacheDto(
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

    private void executeZRemPipelineByRestaurantCacheDtoList(
            RedisConnection connection,
            List<RestaurantCacheDto> restaurantCacheDtoList) {
        connection.openPipeline();
        restaurantCacheDtoList.stream().forEach(restaurantCacheDto ->
                connection.zSetCommands()
                        .zRem(
                                (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                                restaurantCacheDto.toString().getBytes()
                        ));
        connection.closePipeline();
    }




    public void inputRestaurantCache(List<RestaurantCacheDto> restaurantCacheDtoList) {
        if (restaurantCacheDtoList.isEmpty()) {
            log.info("Restaurant 캐싱 종료. 모두 최신 내용 입니다.");
            return;
        }

        log.info("Restaurant {} 건 캐싱 시작",restaurantCacheDtoList.size());
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
