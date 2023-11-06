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
 * RestaurantProcessor에서 전처리한 List<Restaurant> list를  DB에 저장
 * {@link RestaurantWriter#write(List)}
 */
@Component
@RequiredArgsConstructor
public class RestaurantWriter implements ItemWriter<List<Restaurant>> {

    private final RestaurantRepository restaurantRepository;

    /**
     * DB에 restaurantList를 저장합니다.
     * @param restaurantList RestaurantProcessor에서 전처리한 데이터
     * @see com.foodiefinder.datapipeline.processor.RestaurantProcessor
     */
    @Override
    public void write(List<Restaurant> restaurantList) {
        saveAll(restaurantList);
    }

    List<Restaurant> saveAll(List<Restaurant> restaurantList) {
        List<Restaurant> savedResult = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (save(restaurant) == null)
                continue;
            savedResult.add(restaurant);
        }
        return savedResult;
    }

    @Transactional
    Restaurant save(Restaurant restaurant) {
        try {
            return restaurantRepository.save(restaurant);
        } catch (DataIntegrityViolationException e) {
            // 요구사항 : unique 제약조건 위반 시 저장하지 않고 무시
        }
        return null;
    }
}
