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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantCacheSearchRepository {
    private final CacheUtils cacheUtils;
    private final SggCacheRepository sggCacheRepository;

    public List<RestaurantCacheResponse> findAllRestaurantCache(double latitude, double longitude, double range, String orderBy) {
        try(RedisConnection connection = cacheUtils.getConnection()) {
            List<String> nearSGGList = sggCacheRepository.findNearSGG(connection, latitude, longitude);

            List<Object> nearRestaurantSggResult = executeGeoRadiusPipelineByNearSggList(connection, nearSGGList, latitude, longitude, range);

            List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList = cacheUtils.toGeoResultsList(nearRestaurantSggResult);

            List<RestaurantCacheResponse> sggLists = createRestaurantCacheResponseList(geoResultsList);

            return orderRestaurantCacheResponseList(sggLists, orderBy);
        }
    }

    private List<RestaurantCacheResponse> createRestaurantCacheResponseList(List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsList) {
        return geoResultsList.stream()
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
    }

    private List<Object> executeGeoRadiusPipelineByNearSggList(RedisConnection connection,
                                                               List<String> nearSGGList,
                                                               double latitude, double longitude, double range) {
        connection.openPipeline();
        nearSGGList.forEach(
                sgg -> connection.geoCommands()
                        .geoRadius(
                                (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + sgg).getBytes(),
                                cacheUtils.getCircle(latitude, longitude, range, Metrics.KILOMETERS),
                                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending()
                        )
        );
        return connection.closePipeline();
    }

    private List<RestaurantCacheResponse> orderRestaurantCacheResponseList(List<RestaurantCacheResponse> restaurantCacheResponseList, String orderBy) {
        if (orderBy.equals(RestaurantSortOption.RATING.getOption())) {
            return restaurantCacheResponseList.stream()
                    .sorted(Comparator.comparing(RestaurantCacheResponse::getRating).reversed())
                    .toList();
        }

        if (orderBy.equals(RestaurantSortOption.REVIEW_COUNT.getOption())) {
            return restaurantCacheResponseList.stream()
                    .sorted(Comparator.comparing(RestaurantCacheResponse::getReviewCount).reversed())
                    .toList();
        }

        return restaurantCacheResponseList.stream()
                .sorted(Comparator.comparing(RestaurantCacheResponse::getDistance))
                .toList();
    }
}
