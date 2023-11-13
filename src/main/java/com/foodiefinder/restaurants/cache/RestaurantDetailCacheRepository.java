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

    /**
     * 10분 유지
     * 
     * Hash
     * restaurant:{가게 id}
     *
     * List
     * restaurant:{가게 id}:ratings / {유저 id}:{value}:{comment}
     * 20건 정도만 저장
     * - rating
     */
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

    // restaurant:id ex 600
    public RestaurantDetailResponse findByIdFromCache(Long restaurantId) {
        RedisConnection connection = cacheUtils.getConnection();

        if (Boolean.FALSE.equals(connection.keyCommands()
                .exists((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId).getBytes()))) {
            log.info("id {} 는 캐시에 없습니다.",restaurantId);
            return null;
        }

        List<byte[]> fromCache = connection.hashCommands()
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

        if (fromCache == null) {
            return null;
        }

        List<String> restaurantCache = fromCache
                .stream()
                .map(data -> {
                    if (data == null) {
                        return null;
                    }
                    return cacheUtils.decodeFromByteArray(data);
                })
                .toList();

        RestaurantDetailResponse response = new RestaurantDetailResponse();
        response.setId(Long.valueOf(restaurantCache.get(0)));
        response.setSigunName(restaurantCache.get(1));
        response.setBusinessPlaceName(restaurantCache.get(2));
        response.setBusinessStateName(restaurantCache.get(3));
        response.setSanitationBusinessCondition(restaurantCache.get(4));
        response.setRoadAddress(restaurantCache.get(5));
        response.setLotNumberAddress(restaurantCache.get(6));
        response.setZipCode(Double.parseDouble(restaurantCache.get(7)));
        response.setLatitude(Double.parseDouble(restaurantCache.get(8)));
        response.setLongitude(Double.parseDouble(restaurantCache.get(9)));
        response.setAverageRating(Double.parseDouble(restaurantCache.get(10)));

        List<byte[]> ratingsCache = connection.listCommands()
                .lRange(
                        (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId + ":ratings").getBytes(),
                        0, -1
                );

        // 조회시 expire 갱신
        connection.keyCommands()
                .expire((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId).getBytes(),
                        EXPIRE_TIME
                );


        if(ratingsCache != null) {
            List<RatingDto> ratingDtoList = new ArrayList<>();
            ratingsCache.stream()
                    .map(data -> cacheUtils.decodeFromByteArray(data).split(":"))
                    .forEach(data -> {
                        ratingDtoList.add(new RatingDto(Long.valueOf(data[0]), Integer.parseInt(data[1]), data[2]));
                    });
            
            connection.keyCommands()
                    .expire((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurantId + ":ratings").getBytes(),
                            EXPIRE_TIME
                    );
            
            response.setRatings(ratingDtoList);
        }

        log.info("id {} 캐시에서 조회 성공, 만료 시간 갱신.",restaurantId);
        return response;
    }

    public void inputRestaurantDetailCache(Restaurant restaurant) {

        // 평가 10개 이상만 캐싱
        List<Rating> ratings = restaurant.getRatings();
        if (ratings.size() < 10) {
            return;
        }

        log.info("id {} 를 캐시에 저장합니다.",restaurant.getId());
        RedisConnection connection = cacheUtils.getConnection();

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

        connection.hashCommands()
                .hMSet(
                        (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId()).getBytes(),
                        putData
                );

        connection.openPipeline();
        ratings.stream()
                .map(data -> (data.getUser().getId() + ":" + data.getValue() + ":" + data.getComment()).getBytes())
                .forEach(data -> {
                    connection.listCommands()
                            .lPush(
                                    (CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId() + ":ratings").getBytes(),
                                    data
                            );
                });

        connection.keyCommands()
                .expire((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId()).getBytes(),
                        EXPIRE_TIME
                        );

        connection.keyCommands()
                .expire((CacheKeyPrefix.RESTAURANT_KEY_PREFIX.getKeyPrefix() + restaurant.getId() + ":ratings").getBytes(),
                        EXPIRE_TIME
                );
        connection.closePipeline();
        connection.close();
        log.info("id {} 캐시에 저장 완료",restaurant.getId());
    }
}