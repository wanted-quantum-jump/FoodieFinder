package com.foodiefinder.restaurants.controller;

import com.foodiefinder.common.dto.Response;
import com.foodiefinder.restaurants.dto.RestaurantsResponse;
import com.foodiefinder.restaurants.service.RestaurantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantsController {
    private final RestaurantsService restaurantsService;

    @GetMapping
    public Response<RestaurantsResponse> getRestaurants(
            @RequestParam(name = "lat", required = true) String lat
            , @RequestParam(name = "lon", required = true) String lon
            , @RequestParam(name = "range", defaultValue = "1.0") double range
            , @RequestParam(name = "orderBy", defaultValue = "distance") String orderBy) {
        return this.restaurantsService.getRestaurants(lat, lon, range, orderBy);
    }
}
