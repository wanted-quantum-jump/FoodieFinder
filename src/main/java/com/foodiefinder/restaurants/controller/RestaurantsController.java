package com.foodiefinder.restaurants.controller;

import com.foodiefinder.common.dto.Response;
import com.foodiefinder.restaurants.dto.RatingRequest;
import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
import com.foodiefinder.restaurants.service.RatingService;
import com.foodiefinder.restaurants.service.RestaurantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantsController {
    private final RestaurantsService restaurantsService;
    private final RatingService ratingService;

    @GetMapping
    public Response<List<RestaurantDetailResponse>> getRestaurants(
            @RequestParam(name = "lat", required = true) String lat
            , @RequestParam(name = "lon", required = true) String lon
            , @RequestParam(name = "range", defaultValue = "1.0") double range
            , @RequestParam(name = "orderBy", defaultValue = "distance") String orderBy) {
        return this.restaurantsService.getRestaurants(lat, lon, range, orderBy);
    }

    @PostMapping("/{restaurantId}/ratings")
    public Response<Void> createRating(@PathVariable Long restaurantId, @RequestBody RatingRequest dto) {
        return this.ratingService.createRating(restaurantId, dto);

    }

    @GetMapping("/{restaurantId}")
    public Response<RestaurantDetailResponse> getRestaurantDetail(@PathVariable Long restaurantId) {
        return this.restaurantsService.getRestaurantDetail(restaurantId);
    }
}
