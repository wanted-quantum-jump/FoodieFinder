package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.cache.DataPipelineRestaurantCacheRepository;
import com.foodiefinder.datapipeline.writer.dto.RestaurantCacheDto;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.ForCacheRestaurantRepository;
import com.foodiefinder.restaurants.entity.Rating;
import com.foodiefinder.restaurants.entity.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RestaurantCacheWriter implements ItemWriter<List<Restaurant>> {
    private final ForCacheRestaurantRepository forCacheRestaurantRepository;
    private final RatingRepository ratingRepository;
    private final DataPipelineRestaurantCacheRepository dataPipelineRestaurantCacheRepository;

    @Override
    public void write(List<Restaurant> input) {
        List<RestaurantCacheDto> restaurantCacheDtoList = input.stream()
                .map(data -> {
                    Restaurant restaurant = forCacheRestaurantRepository.findByBusinessPlaceNameAndRoadAddress(
                            data.getBusinessPlaceName(),
                            data.getRoadAddress()
                    ).orElse(null);

                    if (restaurant == null) {
                        return null;
                    }

                    List<Rating> ratings = ratingRepository.findByRestaurantId(restaurant.getId());

                    if (ratings.isEmpty()) {
                        return RestaurantCacheDto.setCache(restaurant, 0.0, 0);
                    } else {
                        int count = ratings.size();
                        Double sumRatings = Double.valueOf(ratings.stream()
                                .reduce(0, (total, rating) -> total + rating.getValue(), Integer::sum));
                        return RestaurantCacheDto.setCache(restaurant, (sumRatings / count),count);
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        dataPipelineRestaurantCacheRepository.inputDataPipelineRestaurantCache(restaurantCacheDtoList);
    }
}
