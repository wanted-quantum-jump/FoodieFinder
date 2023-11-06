package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.repository.RawRestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/***
 * RawRestaurant 저장을 위한 Writer 입니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RawRestaurantWriter implements ItemWriter<List<RawRestaurant>> {

    private final RawRestaurantRepository rawRestaurantRepository;

    /**
     * restaurantList를 DB에 저장합니다.
     * @param restaurantList
     */
    @Override
    @Transactional
    public void write(List<RawRestaurant> restaurantList) {
        saveAll(restaurantList);
    }


    // == 저장 메소드 == //
    private List<RawRestaurant> saveAll(List<RawRestaurant> restaurantList) {
        Integer numberOfDuplicates = 0;
        List<RawRestaurant> savedResult = new ArrayList<>();
        for (RawRestaurant rawRestaurant : restaurantList) {
            if (save(rawRestaurant) == null) {
                numberOfDuplicates += 1;
                continue;
            }
            savedResult.add(rawRestaurant);
        }
        log.info("기존 데이터와 중복된 raw는 무시되었습니다. (중복 된 데이터 수  = {})", numberOfDuplicates);
        return savedResult;
    }


    private RawRestaurant save(RawRestaurant rawRestaurant) {
        if (rawRestaurantRepository.findByBusinessPlaceNameAndRoadAddress(rawRestaurant.getBusinessPlaceName(), rawRestaurant.getRoadAddress()) != null) {
            // 요구사항 : unique 제약조건 위반 시 저장하지 않고 무시
            return null;
        }
        return rawRestaurantRepository.save(rawRestaurant);
    }
}
