package com.foodiefinder.restaurants.service;


import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import com.foodiefinder.restaurants.dto.RestaurantsResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantsService {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final String SORT_BY_RATING = "rating";
    private final RestaurantRepository restaurantRepository;


    public Response<RestaurantsResponse> getRestaurants(String lat, String lon, double range, String orderBy) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);

        List<Restaurant> restaurantsWithinRange = restaurantRepository.findAll().stream()
                .filter(restaurant -> calculateDistance(latitude, longitude,
                        restaurant.getLatitude(), restaurant.getLongitude()) <= range)
                .sorted(getComparator(orderBy, latitude, longitude))
                .collect(Collectors.toList());

        ensureRestaurantsFound(restaurantsWithinRange);
        RestaurantsResponse restaurantsDTO = RestaurantsResponse.from(restaurantsWithinRange);

        return Response.success(restaurantsDTO);
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
}
