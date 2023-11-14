package com.foodiefinder.restaurants.cache;

import com.foodiefinder.common.cache.CacheUtils;
import com.foodiefinder.common.enums.CacheKeyPrefix;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.restaurants.dto.RatingDto;
import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
import com.foodiefinder.restaurants.entity.Rating;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantDetailCacheRepository {
    private final CacheUtils cacheUtils;
    private final Long EXPIRE_TIME = 600L;
    private List<byte[]> fieldName;
    
    @PostConstruct
    public void init() {
        fieldName = new ArrayList<>();
        fieldName.add("id".getBytes());
        fieldName.add("sigunName".getBytes());
        fieldName.add("businessPlaceName".getBytes());
        fieldName.add("businessStateName".getBytes());
        fieldName.add("sanitationBusinessCondition".getBytes());
        fieldName.add("roadAddress".getBytes());
        fieldName.add("lotNumberAddress".getBytes());
        fieldName.add("zipCode".getBytes());
        fieldName.add("latitude".getBytes());
        fieldName.add("longitude".getBytes());
        fieldName.add("averageRating".getBytes());
    }

    public RestaurantDetailResponse findByIdFromRestaurantDetailCache(Long restaurantId) {
        try(RedisConnection connection = cacheUtils.getConnection()) {

            List<byte[]> fromCacheRawValue = executeHMGetByRestaurantId(connection, restaurantId);

            if (fromCacheRawValue == null) {
                log.info("id {} 는 캐시에 없습니다.", restaurantId);
                return null;
            }

            List<String> restaurantDetailCacheValue = toStringList(fromCacheRawValue);

            RestaurantDetailResponse response = createRestaurantDetailResponse(restaurantDetailCacheValue);

            List<byte[]> ratingsCacheRawValue = executeLRangeByRestaurantId(connection, restaurantId);

            executeModifyExpire(connection, CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId);


            // 분리
            if (ratingsCacheRawValue != null) {
                List<RatingDto> ratingDtoList = createRatingDtoList(ratingsCacheRawValue);

                executeModifyExpire(connection,
                        CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() +
                                restaurantId +
                                ":ratings"
                );

                response.setRatings(ratingDtoList);
            }

            log.info("id {} 캐시에서 조회 성공, 만료 시간 갱신.", restaurantId);
            return response;
        }
    }

    private List<byte[]> executeHMGetByRestaurantId(RedisConnection connection, Long restaurantId) {
        return connection.hashCommands()
                .hMGet((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId).getBytes(),
                        fieldName.get(0),
                        fieldName.get(1),
                        fieldName.get(2),
                        fieldName.get(3),
                        fieldName.get(4),
                        fieldName.get(5),
                        fieldName.get(6),
                        fieldName.get(7),
                        fieldName.get(8),
                        fieldName.get(9),
                        fieldName.get(10)
                );
    }

    private List<String> toStringList(List<byte[]> bytes) {
        return bytes
                .stream()
                .map(data -> {
                    if (data == null) {
                        return null;
                    }
                    return cacheUtils.decodeFromByteArray(data);
                })
                .toList();
    }

    private RestaurantDetailResponse createRestaurantDetailResponse(List<String> restaurantDetailCacheValue) {
        if (restaurantDetailCacheValue.size() != 11) {
            log.error("필드의 갯수는 11개여야 합니다.");
            return null;
        }
        RestaurantDetailResponse response = new RestaurantDetailResponse();
        response.setId(Long.valueOf(restaurantDetailCacheValue.get(0)));
        response.setSigunName(restaurantDetailCacheValue.get(1));
        response.setBusinessPlaceName(restaurantDetailCacheValue.get(2));
        response.setBusinessStateName(restaurantDetailCacheValue.get(3));
        response.setSanitationBusinessCondition(restaurantDetailCacheValue.get(4));
        response.setRoadAddress(restaurantDetailCacheValue.get(5));
        response.setLotNumberAddress(restaurantDetailCacheValue.get(6));
        response.setZipCode(Double.parseDouble(restaurantDetailCacheValue.get(7)));
        response.setLatitude(Double.parseDouble(restaurantDetailCacheValue.get(8)));
        response.setLongitude(Double.parseDouble(restaurantDetailCacheValue.get(9)));
        response.setAverageRating(Double.parseDouble(restaurantDetailCacheValue.get(10)));
        return response;
    }

    private List<byte[]> executeLRangeByRestaurantId(RedisConnection connection, Long restaurantId) {
        return connection.listCommands()
                .lRange(
                        (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId + ":ratings").getBytes(),
                        0, -1
                );
    }

    private void executeModifyExpire(RedisConnection connection, String key) {
        connection.keyCommands()
                .expire((key).getBytes(),
                        EXPIRE_TIME
                );
    }

    private List<RatingDto> createRatingDtoList(List<byte[]> ratingsCache) {
        List<RatingDto> ratingDtoList = new ArrayList<>();

        ratingsCache.stream()
                .map(data -> cacheUtils.decodeFromByteArray(data).split(":"))
                .forEach(data -> {
                    ratingDtoList.add(new RatingDto(Long.valueOf(data[0]), Integer.parseInt(data[1]), data[2]));
                });

        return ratingDtoList;
    }


    public void inputRestaurantDetailCache(Restaurant restaurant) {

        List<Rating> ratings = restaurant.getRatings();
        if (ratings.size() < 10) {
            return;
        }

        log.info("id {} 를 캐시에 저장합니다.",restaurant.getId());
        try(RedisConnection connection = cacheUtils.getConnection()) {

            Map<byte[], byte[]> putData = createHashMapForHMSet(restaurant);

            executeHMSet(connection, restaurant, putData);

            executeLPushWithPipeline(connection, ratings, restaurant.getId());

            executeModifyExpire(connection, CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId());
            executeModifyExpire(connection, CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId() + ":ratings");

            log.info("id {} 캐시에 저장 완료", restaurant.getId());
        }
    }

    private Map<byte[], byte[]> createHashMapForHMSet(Restaurant restaurant) {
        Map<byte[], byte[]> putData = new HashMap<>();
        putData.put(fieldName.get(0), String.valueOf(restaurant.getId()).getBytes());
        putData.put(fieldName.get(1), restaurant.getSigunName().getBytes());
        putData.put(fieldName.get(2), restaurant.getBusinessPlaceName().getBytes());
        putData.put(fieldName.get(3), restaurant.getBusinessStateName().getBytes());

        putData.put(fieldName.get(4), restaurant.getSanitationBusinessCondition().getBytes());
        putData.put(fieldName.get(5), restaurant.getRoadAddress().getBytes());
        putData.put(fieldName.get(6), restaurant.getLotNumberAddress().getBytes());

        putData.put(fieldName.get(7), String.valueOf(restaurant.getZipCode()).getBytes());
        putData.put(fieldName.get(8), String.valueOf(restaurant.getLatitude()).getBytes());
        putData.put(fieldName.get(9), String.valueOf(restaurant.getLongitude()).getBytes());
        putData.put(fieldName.get(10), String.valueOf(restaurant.getAverageRating()).getBytes());
        return putData;
    }

    private void executeHMSet(RedisConnection connection, Restaurant restaurant, Map<byte[], byte[]> putData) {
        connection.hashCommands()
                .hMSet(
                        (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId()).getBytes(),
                        putData
                );
    }

    private void executeLPushWithPipeline(RedisConnection connection, List<Rating> ratings, Long restaurantId) {
        connection.openPipeline();
        ratings.stream()
                .map(data -> (data.getUser().getId() + ":" + data.getValue() + ":" + data.getComment()).getBytes())
                .forEach(data -> {
                    connection.listCommands()
                            .lPush(
                                    (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId + ":ratings").getBytes(),
                                    data
                            );
                });
        connection.closePipeline();
    }
}