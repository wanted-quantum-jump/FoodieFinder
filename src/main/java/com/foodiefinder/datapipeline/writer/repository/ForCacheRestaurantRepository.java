package com.foodiefinder.datapipeline.writer.repository;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForCacheRestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByBusinessPlaceNameAndRoadAddress(String businessPlaceName, String roadAddress);
}
