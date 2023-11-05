package com.foodiefinder.restaurants.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.dto.RestaurantsResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class RestaurantsServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantsService restaurantsService;


    @Test
    public void 식당을찾지못하면_사용자정의예외를던진다() {
        // Arrange
        String lat = "37.517236";
        String lon = "127.047325";
        double range = 1.0;
        String orderBy = "distance";

        when(restaurantRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        Exception exception = assertThrows(CustomException.class, () ->
                restaurantsService.getRestaurants(lat, lon, range, orderBy)
        );


        assertTrue(exception instanceof CustomException);
        CustomException customException = (CustomException) exception;
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
        assertEquals("R002", customException.getErrorCode().getCode());
        assertEquals("이 지역에는 맛집이 없습니다.", customException.getErrorCode().getMessage());


        verify(restaurantRepository).findAll();
    }
    @Test
    public void 식당을찾으면_성공() {
        // Arrange
        String lat = "37.517236";
        String lon = "127.047325";
        double range = 5.0;
        String orderBy = "distance";

        List<Restaurant> mockRestaurants = Stream.of(
                Restaurant.builder()
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
                        .build(),
                Restaurant.builder()
                        .sigunName("안양시")
                        .businessPlaceName("엘")
                        .businessStateName("영업")
                        .sanitationBusinessCondition("정종/대포집/소주방")
                        .roadAddress("경기도 안양시 동안구 관악대로287번길 24, 지상2층 202호 (관양동)")
                        .lotNumberAddress("경기도 안양시 동안구 관양동 1387-22 지상2층 202호")
                        .zipCode(67890)
                        .latitude(37.517237)
                        .longitude(127.047326)
                        .averageRating(5)
                        .build()

        ).collect(Collectors.toList());

        RestaurantsResponse restaurantsResponse = RestaurantsResponse.from(mockRestaurants);
        Response<RestaurantsResponse> mockResponse = Response.success(restaurantsResponse);

        when(restaurantRepository.findAll()).thenReturn(mockRestaurants);

        // Act
        Response<RestaurantsResponse> response = restaurantsService.getRestaurants(lat, lon, range, orderBy);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getData());
        assertEquals(restaurantsResponse, response.getData());

        // Verify
        verify(restaurantRepository).findAll();
    }
}