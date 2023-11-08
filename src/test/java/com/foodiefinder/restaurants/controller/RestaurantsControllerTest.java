package com.foodiefinder.restaurants.controller;

// <<<<<<< feature/22-rating
import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
// =======
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.ArgumentMatchers.anyLong;
// >>>>>>> develop
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
// <<<<<<< feature/22-rating
import com.foodiefinder.restaurants.dto.RatingRequest;
// =======
// import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
// import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
// >>>>>>> develop
import com.foodiefinder.restaurants.dto.RestaurantsResponse;
import com.foodiefinder.restaurants.service.RatingService;
import com.foodiefinder.restaurants.service.RestaurantsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
// <<<<<<< feature/22-rating

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

// =======
//     @Test
//     public void getRestaurantDetail_ReturnsRestaurantDetails() throws Exception {
//         // Arrange
//         Long restaurantId = 1L;
//         Restaurant mockRestaurant = Restaurant.builder()
//                 .sigunName("TestSigun")
//                 .businessPlaceName("TestPlace")
//                 .businessStateName("Operational")
//                 .sanitationBusinessCondition("Clean")
//                 .roadAddress("123 Test St.")
//                 .lotNumberAddress("123")
//                 .zipCode(12345)
//                 .latitude(37.7749)
//                 .longitude(-122.4194)
//                 .averageRating(5)
//                 .build();
//         RestaurantDetailResponse detailResponse = new RestaurantDetailResponse(mockRestaurant);
//         Response<RestaurantDetailResponse> expectedResponse = Response.success(detailResponse);

//         given(restaurantsService.getRestaurantDetail(restaurantId)).willReturn(expectedResponse);

//         // Act & Assert
//         mockMvc.perform(get("/api/restaurants/{restaurantId}", restaurantId)
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.data").exists());
//     }
// >>>>>>> develop
}
