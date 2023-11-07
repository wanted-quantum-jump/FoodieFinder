package com.foodiefinder.datapipeline.writer.repository;


import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByLatitudeBetweenAndLongitudeBetweenOrderByAverageRatingDesc(
            Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude
    );
}
