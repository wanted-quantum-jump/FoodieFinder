package com.foodiefinder.restaurants.controller;

// <<<<<<< feature/22-rating

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.dto.RatingRequest;
import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
import com.foodiefinder.restaurants.service.RatingService;
import com.foodiefinder.restaurants.service.RestaurantsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class RestaurantsControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private RestaurantsService restaurantsService;
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private RestaurantsController restaurantsController;


    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController).build();
    }

    @Test
    public void 맛집을가져와서_반환한다() throws Exception {
        // Arrange
        String lat = "37.517236";
        String lon = "127.047325";
        double range = 1.0;
        String orderBy = "distance";

        List<RestaurantDetailResponse> restaurantsData = new ArrayList<>();
        restaurantsData.add(RestaurantDetailResponse.from(Restaurant.builder()
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
                .build()));
        Response<List<RestaurantDetailResponse>> mockResponse = Response.success(restaurantsData);

        when(restaurantsService.getRestaurants(anyString(), anyString(), anyDouble(), anyString()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/restaurants")
                        .param("lat", lat)
                        .param("lon", lon)
                        .param("range", String.valueOf(range))
                        .param("orderBy", orderBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Verify
        verify(restaurantsService).getRestaurants(lat, lon, range, orderBy);
    }

    @Test
    void shouldCreateRatingSuccessfully() throws Exception {
        Long restaurantId = 1L;
        RatingRequest dto = new RatingRequest();
        dto.setUserId(123L);
        dto.setValue(5);
        dto.setComment("Great food and service!");
        ArgumentCaptor<RatingRequest> captor = ArgumentCaptor.forClass(RatingRequest.class);

        Response<Void> serviceResponse = Response.success(null);
        when(ratingService.createRating(eq(restaurantId), captor.capture()))
                .thenReturn(serviceResponse);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/ratings", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(ratingService).createRating(eq(restaurantId), captor.capture());
    }

}
