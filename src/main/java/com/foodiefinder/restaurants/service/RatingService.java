package com.foodiefinder.restaurants.service;

import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.cache.RestaurantCacheModifyRatingRepository;
import com.foodiefinder.restaurants.dto.RatingRequest;
import com.foodiefinder.restaurants.entity.Rating;
import com.foodiefinder.restaurants.entity.RatingRepository;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCacheModifyRatingRepository restaurantCacheModifyRatingRepository;

    /**
     * 맛집 평가 캐시 정보 동기화
     */
    public Response<Void> createRating(Long restaurantId, RatingRequest dto) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        User user = getUserById(dto.getUserId());
        createAndSaveRating(user, restaurant, dto);
        updateRestaurantWithAverageRating(restaurant, restaurantId);
        
        restaurantCacheModifyRatingRepository.modifyRatingAtRestaurantCache(restaurant);

        return Response.successVoid();
    }

    private Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void createAndSaveRating(User user, Restaurant restaurant, RatingRequest dto) {
        Rating rating = Rating.builder()
                .user(user)
                .restaurant(restaurant)
                .value(dto.getValue())
                .comment(dto.getComment())
                .build();
        ratingRepository.save(rating);
    }

    private void updateRestaurantWithAverageRating(Restaurant restaurant, Long restaurantId) {
        List<Rating> allRatings = ratingRepository.findByRestaurantId(restaurantId);
        int averageRating = calculateAverage(allRatings);
        restaurant.addRating(averageRating);
        restaurantRepository.save(restaurant);
    }

    private int calculateAverage(List<Rating> ratings) {
        return (int) ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0);
    }
}
