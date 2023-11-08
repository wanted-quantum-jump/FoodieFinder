package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.processor.dto.RootData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


@Slf4j
@DisplayName("단위테스트 - RestaurantProcessor")
class RestaurantProcessorTest {

    String jsonString = """
            {
                "Genrestrtcate": [
                    {
                        "head": [
                            {
                                "list_total_count": 4752
                            },
                            {
                                "RESULT": {
                                    "CODE": "INFO-000",
                                    "MESSAGE": "정상 처리되었습니다."
                                }
                            },
                            {
                                "api_version": "1.0"
                            }
                        ]
                    },
                    {
                        "row": [
                            {
                                "SIGUN_NM": "용인시 ",
                                "SIGUN_CD": null,
                                "BIZPLC_NM": "   토텐   ",
                                "LICENSG_DE": "20091204",
                                "BSN_STATE_NM": "영업",
                                "CLSBIZ_DE": null,
                                "LOCPLC_AR": null,
                                "GRAD_FACLT_DIV_NM": null,
                                "MALE_ENFLPSN_CNT": null,
                                "YY": null,
                                "MULTI_USE_BIZESTBL_YN": null,
                                "GRAD_DIV_NM": null,
                                "TOT_FACLT_SCALE": null,
                                "FEMALE_ENFLPSN_CNT": null,
                                "BSNSITE_CIRCUMFR_DIV_NM": null,
                                "SANITTN_INDUTYPE_NM": null,
                                "SANITTN_BIZCOND_NM": "까페",
                                "TOT_EMPLY_CNT": null,
                                "REFINE_LOTNO_ADDR": "경기도 용인시 기흥구 보정동 1186-4",
                                "REFINE_ROADNM_ADDR": "경기도 용인시 기흥구 죽전로15번길 15-11 (보정동, 1층)",
                                "REFINE_ZIP_CD": "16897",
                                "REFINE_WGS84_LOGT": "   127.1095364820  ",
                                "REFINE_WGS84_LAT": "   37.3218530323    "
                            }
                        ]
                    }
                ]
            }""";


    @Test
    @DisplayName("Restaurant로 변환 성공")
    void convertToRestaurant() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);

        //when
        Restaurant restaurant = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);


    }


    @Test
    @DisplayName("Restaurant로 변환 : 가게명 공백 제거")
    void checkBusinessName() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);

        //when
        Restaurant restaurant = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        Assertions.assertThat(restaurant.getBusinessPlaceName()).isEqualTo("토텐");

    }

    @Test
    @DisplayName("Restaurant로 변환 : 위도")
    void checkLatitude() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);

        //when
        Restaurant restaurant = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        Assertions.assertThat(restaurant.getLatitude()).isEqualTo(37.3218530323D);

    }


    @Test
    @DisplayName("Restaurant로 변환 : 경도")
    void checkLongitude() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);

        //when
        Restaurant restaurant = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        Assertions.assertThat(restaurant.getLongitude()).isEqualTo(127.1095364820D);

    }


    //=== 필수값 누락 체크 == //

    @Test
    @DisplayName("Restaurant로 변환 실패 : 가게명 null인 경우 CustomException 처리")
    void nullBusinessName() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "businessPlaceName", null); //가게명 null로 설정

        //when
        Assertions.assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MISSING_REQUIRED_VALUE.getMessage()); //null 발생시 메시지 출력

    }

    @Test
    @DisplayName("Restaurant로 변환 실패 : 위도에 숫자가 아닌 값")
    void notNumberLatitude() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "latitude", "abcde"); //위도를 숫자가 아닌 값으로 설정

        //than
        Assertions.assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WRONG_NUMBER_FORMAT.getMessage()); //숫자 아닌 값일 때 예외

    }


    @Test
    @DisplayName("Restaurant로 변환 실패 : 경도에 숫자가 아닌 값")
    void notNumberLongitude() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "longitude", "abcde"); //경도를 숫자가 아닌 값으로 설정

        //than
        Assertions.assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WRONG_NUMBER_FORMAT.getMessage()); //숫자 아닌 값일 때 예외

    }

    @Test
    @DisplayName("isCloseDateExist 테스트 - closeDate 공백")
    void isCloseDateExist() {
        String closeDate = "   ";
        Boolean result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "isCloseDateExist", closeDate);
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("isCloseDateExist 테스트 - closeDate null")
    void isCloseDateExist_null() {
        String closeDate = null;
        Boolean result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "isCloseDateExist", closeDate);
        Assertions.assertThat(result).isEqualTo(false);
    }


    @Test
    @DisplayName("변환 성공 - 영업상태 영업이고 폐업 날짜 없는 경우 ")
    void business_state_open() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "businessStateName", "영업"); // 영업상태 "영업"이더라도
        ReflectionTestUtils.setField(raw, "closeDate", ""); // 폐업 날짜 있으면 "폐업" 으로 처리해야함

        //when
        Restaurant result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);
        
        //than
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBusinessStateName()).isEqualTo("영업");
    }


    @Test
    @DisplayName("변환 실패 - 영업상태 영업이고 폐업 날짜 있는 경우 (폐업 날짜가 있으면 폐업으로 분류한다)")
    void closeDate_state_is_open_but_closeDate_exist() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "businessStateName", "영업"); // 영업상태 "영업"이지만
        ReflectionTestUtils.setField(raw, "closeDate", "2023-11-04"); // 폐업 날짜 있으면 폐업으로 분류하여 저장하지 않음

        //when
        Restaurant result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        //변환 실패시 convertToRestaurant은 null을 리턴해야한다.
        Assertions.assertThat(result).isNull();
    }


    @Test
    @DisplayName("변환 실패 - 영업상태 폐업이고 폐업 날짜 없는 경우 폐업으로 분류")
    void  closeDate_state_is_close_but_closeDate_exist() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "businessStateName", "폐업"); // 영업상태 "폐업"일때
        ReflectionTestUtils.setField(raw, "closeDate", ""); // 폐업 날짜 없음

        //when
        Restaurant result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        //변환 실패시 convertToRestaurant는 null을 리턴해야한다.
        Assertions.assertThat(result).isNull();
    }

    @Test
    @DisplayName("변환 실패 - 영업상태 폐업이고 폐업 날짜 있는 경우 폐업으로 분류")
    void closeDate_state_is_close() {

        //given
        RawRestaurant raw = RootData.of(jsonString).toEntityList().get(0);
        ReflectionTestUtils.setField(raw, "businessStateName", "폐업"); // 영업상태 "폐업"일때
        ReflectionTestUtils.setField(raw, "closeDate", "2023-11-04"); // 폐업 날짜 없으면 그대로 유지 (영업으로 변경하지 않음)

        //when
        Restaurant result = ReflectionTestUtils.invokeMethod(RestaurantProcessor.class, "convertToRestaurant", raw);

        //than
        //변환 실패시 convertToRestaurant는 null을 리턴해야한다.
        Assertions.assertThat(result).isNull();
    }
}