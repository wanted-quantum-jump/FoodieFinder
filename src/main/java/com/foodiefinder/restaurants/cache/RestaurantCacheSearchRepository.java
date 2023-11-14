package com.foodiefinder.restaurants.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.cache.SggCacheRepository;
import com.foodiefinder.restaurants.dto.RestaurantCacheResponse;
import com.foodiefinder.restaurants.enums.RestaurantSortOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Geo 사용
 * key = 지역:
 * lon/lat = lon/lat
 * member = id
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantCacheSearchRepository {
    private final CacheUtils cacheUtils;
    private final SggCacheRepository sggCacheRepository;


    /**
     * 위치 정보를 받아, Redis 에서 거리, 평점, 리뷰 수 순으로 리스트를 받을 수 있다.
     * @param latitude  위도
     * @param longitude 경도
     * @param range     범위
     * @param orderBy   rating, distance, review
     * @return orderBy 에 의해 정렬된 RestaurantCacheResponse 출력
     */
    public List<RestaurantCacheResponse> findRestaurantCache(double latitude, double longitude, double range, String orderBy) {
        RedisConnection connection = cacheUtils.getConnection();
        List<String> nearSGGList = sggCacheRepository.findNearSGG(connection, latitude, longitude);

        connection.openPipeline();
        nearSGGList.stream().forEach(
                sgg -> {
                    connection.geoCommands()
                            .geoRadius(
                                    (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + sgg).getBytes(),
                                    cacheUtils.getCircle(latitude, longitude, range, Metrics.KILOMETERS),
                                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending()
                            );
                }
        );
        List<Object> nearRestaurantSggResult = connection.closePipeline();
        connection.close();

        List<RestaurantCacheResponse> sggLists = nearRestaurantSggResult.stream()
                .map(data -> {
                    if (data instanceof GeoResults<?>) {
                        return (GeoResults<RedisGeoCommands.GeoLocation<byte[]>>) data;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(data -> data.getContent().stream())
                .map(geoResult -> {
                    String[] member = cacheUtils.decodeFromByteArray(geoResult.getContent().getName()).split(":");
                    Long id = Long.valueOf(member[0]);
                    Double rating = Double.valueOf(member[1]);
                    Long reviewCount = Long.valueOf(member[2]);
                    String businessPlaceName = member[3];
                    String sanitationBusinessCondition = member[4];
                    Double distance = geoResult.getDistance().getValue();
                    return RestaurantCacheResponse.fromCache(id, rating, reviewCount, businessPlaceName, sanitationBusinessCondition, distance);
                })
                .toList();

        /**
         * 평점, 리뷰, 거리순 정렬
         */
        // 평점 높은
        if (orderBy.equals(RestaurantSortOption.RATING.getOption())) {
            return sggLists.stream()
                    .sorted(Comparator.comparing(RestaurantCacheResponse::getRating).reversed())
                    .toList();
        }
        // 리뷰 많은
        else if (orderBy.equals(RestaurantSortOption.REVIEW_COUNT.getOption())) {
            return sggLists.stream()
                    .sorted(Comparator.comparing(RestaurantCacheResponse::getReviewCount).reversed())
                    .toList();
        }
        // 거리 가까운
        else {
            return sggLists.stream()
                    .sorted(Comparator.comparing(RestaurantCacheResponse::getDistance))
                    .toList();
        }
    }
}
