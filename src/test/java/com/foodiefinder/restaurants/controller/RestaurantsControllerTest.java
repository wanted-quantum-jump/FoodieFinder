package com.foodiefinder.restaurants.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
import com.foodiefinder.restaurants.dto.RestaurantsResponse;
import com.foodiefinder.restaurants.service.RestaurantsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
public class RestaurantsControllerTest {
    private MockMvc mockMvc;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private RestaurantsService restaurantsService;

    @InjectMocks
    private RestaurantsController restaurantsController;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController).build();
    }

    @Test
    public void 맛집을가져와서_반환한다() throws Exception {
        // Arrange
        String lat = "37.517236";
        String lon = "127.047325";
        double range = 1.0;
        String orderBy = "distance";

        List<Restaurant> restaurantList = new ArrayList<>();
        RestaurantsResponse restaurantsData = RestaurantsResponse.from(restaurantList);
        Response<RestaurantsResponse> mockResponse = Response.success(restaurantsData);

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
    public void getRestaurantDetail_ReturnsRestaurantDetails() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        Restaurant mockRestaurant = Restaurant.builder()
                .sigunName("TestSigun")
                .businessPlaceName("TestPlace")
                .businessStateName("Operational")
                .sanitationBusinessCondition("Clean")
                .roadAddress("123 Test St.")
                .lotNumberAddress("123")
                .zipCode(12345)
                .latitude(37.7749)
                .longitude(-122.4194)
                .averageRating(5)
                .build();
        RestaurantDetailResponse detailResponse = new RestaurantDetailResponse(mockRestaurant);
        Response<RestaurantDetailResponse> expectedResponse = Response.success(detailResponse);

        given(restaurantsService.getRestaurantDetail(restaurantId)).willReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurantId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists());
    }
}
