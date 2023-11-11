package com.foodiefinder.restaurants.service;


import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.cache.RestaurantCacheRepository;
import com.foodiefinder.restaurants.cache.RestaurantDetailCacheRepository;
import com.foodiefinder.restaurants.dto.RestaurantCacheResponse;
import com.foodiefinder.restaurants.dto.RestaurantDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantsService {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final String SORT_BY_RATING = "rating";
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCacheRepository restaurantCacheRepository;
    private final RestaurantDetailCacheRepository restaurantDetailCacheRepository;

    /**
     * 맛집 목록 캐싱
     */
    public Response<List<RestaurantCacheResponse>> getRestaurantsFromCache(String lat, String lon, double range, String orderBy) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        List<RestaurantCacheResponse> restaurantCacheRespons = restaurantCacheRepository.createRestaurantsCacheResponse(latitude, longitude, range, orderBy);

        return Response.success(restaurantCacheRespons);
    }

    public Response<List<RestaurantDetailResponse>> getRestaurants(String lat, String lon, double range, String orderBy) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);

        List<Restaurant> restaurantsWithinRange = restaurantRepository.findAll().stream()
                .filter(restaurant -> calculateDistance(latitude, longitude,
                        restaurant.getLatitude(), restaurant.getLongitude()) <= range)
                .sorted(getComparator(orderBy, latitude, longitude)).toList();

        ensureRestaurantsFound(restaurantsWithinRange);
        List<RestaurantDetailResponse> result = new ArrayList<>();
        for (Restaurant restaurant : restaurantsWithinRange) {
            RestaurantDetailResponse dto = RestaurantDetailResponse.from(restaurant);
            result.add(dto);
        }
        return Response.success(result);
    }

    private void ensureRestaurantsFound(List<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            throw new CustomException(ErrorCode.NO_RESTAURANTS_IN_RANGE);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private Comparator<Restaurant> getComparator(String orderBy, double latitude, double longitude) {
        if (SORT_BY_RATING.equals(orderBy)) {
            return Comparator.comparing(Restaurant::getAverageRating, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return Comparator.comparingDouble((Restaurant r) ->
                calculateDistance(latitude, longitude, r.getLatitude(), r.getLongitude()));
    }

    /**
     * 맛집 상세정보 캐싱
     */
    public Response<RestaurantDetailResponse> getRestaurantDetail(Long restaurantId) {

        RestaurantDetailResponse responseFromCache = restaurantDetailCacheRepository.findByIdFromCache(restaurantId);
        if (responseFromCache != null) {
            return Response.success(responseFromCache);
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        restaurantDetailCacheRepository.inputRestaurantDetailCache(restaurant);

        RestaurantDetailResponse detailResponse = RestaurantDetailResponse.from(restaurant);
        return Response.success(detailResponse);
    }
}
