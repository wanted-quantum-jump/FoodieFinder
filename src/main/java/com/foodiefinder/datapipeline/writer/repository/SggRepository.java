package com.foodiefinder.datapipeline.writer.repository;


import com.foodiefinder.datapipeline.writer.entity.Sgg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SggRepository extends JpaRepository<Sgg, Long> {

    Sgg findByDosiAndSgg(String dosi, String sgg);
}
