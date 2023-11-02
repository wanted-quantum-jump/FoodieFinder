package com.foodiefinder.datapipeline.writer.repository;


import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawRestaurantRepository extends JpaRepository<RawRestaurant, Long> {

}
