package com.foodiefinder.datapipeline.writer.repository;

import com.foodiefinder.config.RepositoryUnitTest;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;


@RepositoryUnitTest
@DisplayName("단위테스트 - RestaurantRepository ")
class RestaurantRepositoryTest {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("저장 성공")
    void fullFilledRestaurant() {
        Restaurant restaurant = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드 파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        //when
        restaurantRepository.save(restaurant);
        //than
        Assertions.assertThatNoException().isThrownBy(() -> restaurantRepository.save(restaurant));
        Assertions.assertThat(restaurant.getId()).isNotNull();

    }

    @Test
    @DisplayName("유니크 제약조건 : 가게이름 + 도로명 주소와 유니크 제약조건 위반 ")
    void checkUnique() {
        //given
        String SAME_ROADADDR = "경기도 화성시 반월동 869 (111호)";
        String SAME_BUSINESS_PLACE_NAME = "푸드 파인더 카페";

        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName(SAME_BUSINESS_PLACE_NAME) //same
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress(SAME_ROADADDR) //same
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName(SAME_BUSINESS_PLACE_NAME) //same
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress(SAME_ROADADDR) //same
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        restaurantRepository.save(restaurant1);
        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant2)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("유니크 제약조건: 가게이름이 같아도 도로명 주소 다르면 저장 가능")
    void checkDiffBusinessPlaceName() {
        //given
        String SAME_BUSINESS_PLACE_NAME = "푸드 파인더 카페";

        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName(SAME_BUSINESS_PLACE_NAME) //same
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 어딘가") // diff
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName(SAME_BUSINESS_PLACE_NAME) //same
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)") // diff
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        restaurantRepository.save(restaurant1);
        //when
        //than
        Assertions.assertThatNoException().isThrownBy(() -> restaurantRepository.save(restaurant2));
    }

    @Test
    @DisplayName("유니크 제약조건 : 도로명 주소 같아도 가게이름 다르면 저장 가능")
    void checkDiffRoadAddress() {
        //given
        String SAME_ROADADDR = "경기도 화성시 반월동 869 (111호)";

        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페") // diff
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress(SAME_ROADADDR) // same
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("스타벅스 화성점") // diff
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress(SAME_ROADADDR) // same
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(37.12345)
                .longitude(127.12345)
                .build();

        restaurantRepository.save(restaurant1);
        //when
        //than
        Assertions.assertThatNoException().isThrownBy(() -> restaurantRepository.save(restaurant2));
    }


    @Test
    @DisplayName("null 제약조건 : 위도는 null 일 수 없다.")
    void checkNullLat() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(null) //null
                .longitude(127.12345)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 경도는 null 일 수 없다.")
    void checkNullLon() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(35.123)
                .longitude(null)//null
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 가게이름은 null 일 수 없다.")
    void checkNullBusinessPlaceName() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName(null) //null
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(35.123)
                .longitude(123.12412)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 도로명주소는 null 일 수 없다.")
    void checkNullRoadAddress() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress(null) //null
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(35.123)
                .longitude(123.12412)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 영업상태는 null 일 수 없다.")
    void checkNullStateName() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName(null)//null
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(35.123)
                .longitude(123.12412)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    @DisplayName("null 제약조건 : 시군구는 null 일 수 없다.")
    void checkNullSigunName() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName(null) //null
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(35.123)
                .longitude(123.12412)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 위생업태명은 null 일 수 없다.")
    void checkNullSanitationBusinessCondition() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition(null)//null
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress("경기도 화성시 영통로 59")
                .zipCode(12345)
                .latitude(23.4)
                .longitude(127.12345)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("null 제약조건 : 지번주소는 null 일 수 없다.")
    void checkNullLotNumberAddress() {
        //given
        Restaurant restaurant1 = Restaurant.builder()
                .sigunName("화성시")
                .businessPlaceName("푸드파인더 카페")
                .businessStateName("영업")
                .sanitationBusinessCondition("카페")
                .roadAddress("경기도 화성시 반월동 869 (111호)")
                .lotNumberAddress(null) //null
                .zipCode(12345)
                .latitude(35.123)
                .longitude(123.12412)
                .build();

        //when
        //than
        Assertions.assertThatThrownBy(() -> restaurantRepository.save(restaurant1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}