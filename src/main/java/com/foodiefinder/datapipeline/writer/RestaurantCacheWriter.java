package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.cache.DataPipelineRestaurantCacheRepository;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.ForCacheRestaurantRepository;
import com.foodiefinder.restaurants.entity.Rating;
import com.foodiefinder.restaurants.entity.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantCacheWriter implements ItemWriter<List<Restaurant>> {
    private final ForCacheRestaurantRepository forCacheRestaurantRepository;
    private final RatingRepository ratingRepository;
    private final DataPipelineRestaurantCacheRepository dataPipelineRestaurantCacheRepository;

    @Override
    public void write(List<Restaurant> input) {
        List<RestaurantCacheDto> restaurantCacheDtoList = input.stream()
                .map(this::toRestaurantCacheDto)
                .filter(Objects::nonNull)
                .toList();

        List<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>> geoResultsListWithPipelineByRestaurantDtoList =
                dataPipelineRestaurantCacheRepository.findGeoResultsListByRestaurantDtoList(restaurantCacheDtoList);

        List<RestaurantCacheDto> needUpdateRestaurantCacheDtoList =
                dataPipelineRestaurantCacheRepository.needUpdateRestaurantCacheDtoList(
                        geoResultsListWithPipelineByRestaurantDtoList,
                        restaurantCacheDtoList);

        if (needUpdateRestaurantCacheDtoList.isEmpty()) {
            log.info("Restaurant 캐싱 종료. 모두 최신 내용 입니다.");
            return;
        }

        dataPipelineRestaurantCacheRepository.inputRestaurantCache(needUpdateRestaurantCacheDtoList);
    }

    /**
     * 상호명, 거리명주소로 Restaurant 를 찾아 캐싱을 하기 위한 RestaurantCacheDto 로 변환
     * @param restaurant
     * @return
     */
    private RestaurantCacheDto toRestaurantCacheDto(Restaurant restaurant) {
        Restaurant foundRestaurant = forCacheRestaurantRepository.findByBusinessPlaceNameAndRoadAddress(restaurant.getBusinessPlaceName(), restaurant.getRoadAddress())
                .orElse(null);

        if (foundRestaurant == null) {
            return null;
        }

        List<Rating> ratings = ratingRepository.findByRestaurantId(restaurant.getId());
        return createRestaurantCacheDto(foundRestaurant, ratings);
    }

    private RestaurantCacheDto createRestaurantCacheDto(Restaurant restaurant, List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return RestaurantCacheDto.setCache(restaurant, 0.0, 0);
        }

        int count = ratings.size();
        Double sumRatings = Double.valueOf(ratings.stream()
                .reduce(0, (total, rating) -> total + rating.getValue(), Integer::sum));
        return RestaurantCacheDto.setCache(restaurant, (sumRatings / count),count);
    }
}
