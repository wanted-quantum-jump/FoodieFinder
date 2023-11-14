package com.foodiefinder.restaurants.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.restaurants.entity.Rating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantCacheForModifyRatingRepository {
    private final CacheUtils cacheUtils;

    public void modifyRatingAtRestaurantCache(Restaurant restaurant) {
        try (RedisConnection connection = cacheUtils.getConnection()) {
            log.info("id {} 에 대한 캐시 동기화",restaurant.getId());

            RestaurantCacheDto restaurantCacheDto = createRestaurantCacheDto(restaurant.getRatings(), restaurant);

            GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoResults = executeGeoRadiusByRestaurantCacheDto(connection, restaurantCacheDto);
            
            if (hasGeoResult(geoResults)) {
                geoResults.getContent()
                        .stream()
                        .filter(data -> isGeoResultContentMatchRestaurantId(data, restaurantCacheDto))
                        .forEach(data -> executeZRemMapRestaurantCacheDto(
                                connection, data, restaurantCacheDto
                        ));
            }

            executeGeoAddRestaurantCacheDto(connection, restaurantCacheDto);
            log.info("id {} 에 대한 캐시 동기화 완료",restaurant.getId());
        }
    }

    private void executeGeoAddRestaurantCacheDto(
            RedisConnection connection,
            RestaurantCacheDto restaurantCacheDto) {
        connection.geoCommands()
                .geoAdd(
                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                        new Point(restaurantCacheDto.getLongitude(), restaurantCacheDto.getLatitude()),
                        (restaurantCacheDto.toString()).getBytes()
                );
    }

    private void executeZRemMapRestaurantCacheDto(
            RedisConnection connection,
            GeoResult<RedisGeoCommands.GeoLocation<byte[]>> geoLocation,
            RestaurantCacheDto restaurantCacheDto
    ) {
        connection.zSetCommands()
                .zRem(
                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                        geoLocation.getContent().getName()
                );
    }

    private boolean isGeoResultContentMatchRestaurantId(
            GeoResult<RedisGeoCommands.GeoLocation<byte[]>> geoLocation,
            RestaurantCacheDto restaurantCacheDto) {
        return Long.valueOf(cacheUtils.decodeFromByteArray(geoLocation.getContent().getName()).split(":")[0]).equals(restaurantCacheDto.getId());
    }

    private boolean hasGeoResult(GeoResults<?> geoResults) {
        if (geoResults == null) {
            return false;
        }

        return !geoResults.getContent().isEmpty();
    }

    private GeoResults<RedisGeoCommands.GeoLocation<byte[]>> executeGeoRadiusByRestaurantCacheDto(
            RedisConnection connection,
            RestaurantCacheDto restaurantCacheDto) {

        return connection.geoCommands()
                .geoRadius(
                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                        cacheUtils.getCircle(restaurantCacheDto.getLatitude(), restaurantCacheDto.getLongitude(), 0.01, Metrics.KILOMETERS)
                );
    }

    private RestaurantCacheDto createRestaurantCacheDto(List<Rating> ratings, Restaurant restaurant) {
        return RestaurantCacheDto.setCache(
                restaurant,
                sumRatings(ratings) / ratings.size(),
                ratings.size()
        );
    }
    private Double sumRatings(List<Rating> ratings) {
        return Double.valueOf(ratings.stream()
                .reduce(0, (total, rating) -> total + rating.getValue(), Integer::sum));
    }
}
