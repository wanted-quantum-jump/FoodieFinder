package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.repository.RawRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RawRestaurantWriter implements ItemWriter<List<RawRestaurant>> {

    private final RawRestaurantRepository rawRestaurantRepository;

    @Override
    @Transactional
    public void write(List<RawRestaurant> restaurantList) {
        saveAll(restaurantList);
    }


    private List<RawRestaurant> saveAll(List<RawRestaurant> restaurantList) {
        List<RawRestaurant> savedResult = new ArrayList<>();
        for (RawRestaurant rawRestaurant : restaurantList) {
            if (save(rawRestaurant) == null)
                continue;
            savedResult.add(rawRestaurant);
        }
        return savedResult;
    }

    private RawRestaurant save(RawRestaurant rawRestaurant) {
        try {
            return rawRestaurantRepository.save(rawRestaurant);
        } catch (DataIntegrityViolationException e) {
            // 요구사항 : unique 제약조건 위반 시 저장하지 않고 무시
        }
        return null;
    }
}
