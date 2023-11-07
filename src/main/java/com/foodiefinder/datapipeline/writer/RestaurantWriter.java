package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/***
 * RestaurantProcessor에서 전처리한 List<Restaurant> list를  DB에 저장
 * {@link RestaurantWriter#write(List)}
 */
@Slf4j
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
        if (restaurantList.size() <= 0)
            return;
        log.info("Restaurant-{} 데이터 저장 시작", restaurantList.get(0).getSanitationBusinessCondition());
        List<Restaurant> result = saveAll(restaurantList);
        log.info("Restaurant-{} 데이터 저장 종료 ({}개의 새로운 데이터가 저장되었고, {}개의 중복 데이터는 저장되지 않았습니다.)", restaurantList.get(0).getSanitationBusinessCondition(), result.size(), restaurantList.size() - result.size());
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

        if (restaurantRepository.findByBusinessPlaceNameAndRoadAddress(restaurant.getBusinessPlaceName(), restaurant.getRoadAddress()) != null)
        { // 요구사항 : unique 제약조건 위반 시 저장하지 않고 무시
            return null;
        }

        return restaurantRepository.save(restaurant);

    }
}
