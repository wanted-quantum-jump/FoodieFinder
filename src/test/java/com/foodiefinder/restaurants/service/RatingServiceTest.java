package com.foodiefinder.restaurants.service;

import com.foodiefinder.restaurants.entity.RatingRepository;
import com.foodiefinder.restaurants.dto.RatingRequest;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    public void createRating_WithValidData_ReturnsSuccess() {
        // Arrange
        Long restaurantId = 1L;
        Long userId = 1L;
        Restaurant mockRestaurant = Restaurant.builder()
                .sigunName("안양시")
                .businessPlaceName("시시마루")
                .businessStateName("영업")
                .sanitationBusinessCondition("정종/대포집/소주방")
                .roadAddress("경기도 안양시 동안구 관평로182번길 23 (관양동, 무지개상가103호)")
                .lotNumberAddress("경기도 안양시 동안구 관양동 1602-6번지")
                .zipCode(12345)
                .latitude(37.517236)
                .longitude(127.047325)
                .averageRating(4)
                .build();
        User mockUser = User.builder()
                .account("test")
                .password("test")
                .build();

        RatingRequest mockRatingRequest = new RatingRequest();
        mockRatingRequest.setUserId(userId);
        mockRatingRequest.setValue(5);
        mockRatingRequest.setComment("Great food!");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(mockRestaurant));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(ratingRepository.findByRestaurantId(restaurantId)).thenReturn(
                Collections.emptyList());

        // Act
        Response<Void> response = ratingService.createRating(restaurantId, mockRatingRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());


        verify(ratingRepository).save(argThat(rating ->
                rating.getUser().equals(mockUser) &&
                        rating.getRestaurant().equals(mockRestaurant) &&
                        rating.getValue() == mockRatingRequest.getValue() &&
                        rating.getComment().equals(mockRatingRequest.getComment())
        ));

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    public void createRating_WhenRestaurantNotFound_ThrowsException() {
        // Arrange
        Long restaurantId = 1L;
        Long userId = 1L;
        RatingRequest mockRatingRequest = new RatingRequest();
        mockRatingRequest.setUserId(userId);
        mockRatingRequest.setValue(5);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () ->
                ratingService.createRating(restaurantId, mockRatingRequest)
        );

        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).findById(anyLong());
        verify(ratingRepository, never()).save(any());
    }
}