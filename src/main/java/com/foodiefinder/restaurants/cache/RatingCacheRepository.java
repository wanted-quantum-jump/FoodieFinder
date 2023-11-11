package com.foodiefinder.restaurants.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.cache.DataPipelineSggCacheRepository;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.restaurants.entity.Rating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RatingCacheRepository {
    private final CacheUtils cacheUtils;
    private final DataPipelineSggCacheRepository sggCacheRepository;

    /**
     * 유저가 평가할떄 redis 와 동기화 하기 위한 메서드
     */
    public void inputRatingCache(Restaurant restaurant) {
        log.info("id {} 에 대한 캐시 동기화",restaurant.getId());

        Double latitude = restaurant.getLatitude();
        Double longitude = restaurant.getLongitude();

        List<Rating> ratings = restaurant.getRatings();
        int count = ratings.size();
        Double sumRatings = Double.valueOf(ratings.stream()
                .reduce(0, (total, rating) -> total + rating.getValue(), Integer::sum));

        RestaurantCacheDto restaurantCacheDto = RestaurantCacheDto.setCache(restaurant, sumRatings / count, count);

        RedisConnection connection = cacheUtils.getConnection();

        List<String> nearSGGList = sggCacheRepository.findNearSGG(connection, latitude, longitude, 1000);

        nearSGGList.stream()
                .map(
                        sgg -> connection.geoCommands()
                                .geoRadius(
                                        (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + sgg).getBytes(),
                                        cacheUtils.getCircle(latitude, longitude, 0.1, Metrics.KILOMETERS))
                )
                .filter(data -> data.getContent().size() > 0)
                .flatMap(data -> data.getContent().stream())
                .filter(data -> Long.valueOf(cacheUtils.decodeFromByteArray(data.getContent().getName()).split(":")[0]).equals(restaurant.getId()))
                .forEach(data -> {
                    connection.zSetCommands()
                            .zRem(
                                    (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                                    data.getContent().getName()
                            );

                    connection.geoCommands()
                            .geoAdd(
                                    (CacheKeyPrefix.MAP_SGG.getKeyPrefix() + restaurantCacheDto.getSigunName()).getBytes(),
                                    new Point(restaurant.getLongitude(), restaurant.getLatitude()),
                                    (restaurantCacheDto.toString()).getBytes()
                            );
                });
        connection.close();
    }
}
