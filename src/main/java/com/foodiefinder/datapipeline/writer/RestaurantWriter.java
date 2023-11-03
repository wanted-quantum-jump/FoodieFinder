package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/***
 * RestaurantProcessor에서 전처리한 List<Restaurant> list를  Restaurant에 저장
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantWriter implements ItemWriter<List<Restaurant>> {

    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public void write(List<Restaurant> restaurantList) {
        saveAll(restaurantList);
    }

    @Transactional
    List<Restaurant> saveAll(List<Restaurant> restaurantList) {
        List<Restaurant> savedResult = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (save(restaurant) == null)
                continue;
            savedResult.add(restaurant);
        }
        return savedResult;
    }

    private Restaurant save(Restaurant restaurant) {
        try {
            return restaurantRepository.save(restaurant);
        } catch (DataIntegrityViolationException e) {
            // 요구사항 : unique 제약조건 위반 시 저장하지 않고 무시
        }
        return null;
    }
}
