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
     * @param rawRestaurantList
     */
    @Override
    @Transactional
    public void write(List<RawRestaurant> rawRestaurantList) {
        if (rawRestaurantList.size() <= 0)
            return;
        log.info("RawRestaurant-{} 데이터 저장 시작", rawRestaurantList.get(0).getSanitationBusinessCondition());
        List<RawRestaurant> result = saveAll(rawRestaurantList);
        log.info("RawRestaurant-{} 데이터 저장 종료 ({}개의 새로운 데이터가 저장되었고, {}개의 중복 데이터는 저장되지 않았습니다.)", rawRestaurantList.get(0).getSanitationBusinessCondition(), result.size(), rawRestaurantList.size() - result.size());
    }


    // == 저장 메소드 == //
    private List<RawRestaurant> saveAll(List<RawRestaurant> rawRestaurantList) {
        List<RawRestaurant> savedResult = new ArrayList<>();
        for (RawRestaurant rawRestaurant : rawRestaurantList) {
            if (save(rawRestaurant) == null) {
                continue;
            }
            savedResult.add(rawRestaurant);
        }
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
