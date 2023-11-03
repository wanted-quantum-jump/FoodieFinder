package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * RawRestaurant list -> Restaurant list로 가공
 * @see RawRestaurantProcessor
 */
@Component
public class RestaurantProcessor implements ItemProcessor<List<RawRestaurant>, List<Restaurant>> {

    /**
     * @param rawRestaurantList RawRestaurantProcessor에서 전처리한 데이터
     * @return DB에 저장할 수 있게 가공된 Restaurant list
     */
    @Override
    public List<Restaurant> process(final List<RawRestaurant> rawRestaurantList) {
        List<Restaurant> result = new ArrayList<>();

        //Raw -> Restaurant
        for (RawRestaurant raw : rawRestaurantList) {
            try {
                Restaurant restaurant = convertToRestaurant(raw);
                result.add(restaurant);
            } catch (CustomException e) {
                // CustomException 발생하면 무시하고 해당 식당 정보는 저장하지 않음
            }
        }
        return result;
    }

    /**
     * RawRestaurant를 Restaurant로 변환합니다. <br>
     * - String에서 공백 제거 <br>
     * - 필요시 String을 숫자로 변환<br>
     *
     * @param raw
     * @return
     */
    private static Restaurant convertToRestaurant(final RawRestaurant raw) {
        try {
            Restaurant restaurant = Restaurant
                    .builder()
                    .sigunName(raw.getSigunName().trim())
                    .businessStateName(raw.getBusinessStateName().trim())
                    .businessPlaceName(raw.getBusinessPlaceName().trim())
                    .sanitationBusinessCondition(raw.getSanitationBusinessCondition().trim())
                    .roadAddress(raw.getRoadAddress().trim())
                    .lotNumberAddress(raw.getLotNumberAddress().trim())
                    .zipCode(Integer.parseInt(raw.getZipCode())) //String to Int
                    .latitude(Double.parseDouble(raw.getLatitude())) //String to Double
                    .longitude(Double.parseDouble(raw.getLongitude())) //String to Double
                    .build();
            return restaurant;
        } catch (NullPointerException e) {
            //필수 값 중 null이 있는 경우
            throw new CustomException(ErrorCode.MISSING_REQUIRED_VALUE);
        } catch (NumberFormatException e) {
            // zipCode, latitude, longitude에 숫자가 아닌 값이 들어온 경우
            throw new CustomException(ErrorCode.WRONG_NUMBER_FORMAT);
        }
    }

}