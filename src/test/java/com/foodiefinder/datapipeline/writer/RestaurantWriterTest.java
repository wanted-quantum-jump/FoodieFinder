package com.foodiefinder.datapipeline.writer;

import com.foodiefinder.datapipeline.processor.RestaurantProcessor;
import com.foodiefinder.datapipeline.processor.dto.RootData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.datapipeline.writer.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Slf4j
@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("단위테스트 - RestaurantWriter")
class RestaurantWriterTest {

    @InjectMocks
    RestaurantWriter restaurantWriter;

    @Mock
    RestaurantRepository restaurantRepository;


    RestaurantProcessor restaurantProcessor = new RestaurantProcessor();

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
                                "BIZPLC_NM": "토텐",
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
                                "REFINE_WGS84_LOGT": "127.1095364820",
                                "REFINE_WGS84_LAT": "37.3218530323"
                            },
                            {
                                "SIGUN_NM": "파주시 ",
                                "SIGUN_CD": null,
                                "BIZPLC_NM": "베로키오",
                                "LICENSG_DE": "20120423",
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
                                "REFINE_LOTNO_ADDR": "경기도 파주시 탄현면 성동리 87-3번지 1층",
                                "REFINE_ROADNM_ADDR": "경기도 파주시 탄현면 새오리로 74, 1층",
                                "REFINE_ZIP_CD": "10858",
                                "REFINE_WGS84_LOGT": "126.6856377315",
                                "REFINE_WGS84_LAT": "37.7908765695"
                            }
                        ]
                    }
                ]
            }""";

    @Test
    void write() {
        //given
        List<RawRestaurant> rawDatalist = RootData.of(jsonString).toEntityList();
        List<Restaurant> restaurantList = restaurantProcessor.process(rawDatalist); //raw 데이터 전처리해서 Restaurant로 변환

        //when
        List<Restaurant> rawRestaurants = restaurantRepository.saveAll(restaurantList);

        for (Restaurant r : rawRestaurants) {
            log.info("id = {}", r.getId());
        }
    }

    @DisplayName("요구사항 : 데이터를 중복 저장 시도할 경우 무시")
    @Test
    void 유니크조건위반(){
        List<RawRestaurant> rawDatalist = RootData.of(jsonString).toEntityList();
        List<Restaurant> restaurantList = restaurantProcessor.process(rawDatalist); //raw 데이터 전처리해서 Restaurant로 변환

        Restaurant restaurant1 = restaurantList.get(0);
        Restaurant restaurant2 = restaurantList.get(1);

        //raw1, raw2 가 유니크 제약 조건 {roadAddress, businessPlaceName} 위반하도록 같은 값 집어넣음
        String SAME_ADDRESS = "도로명주소";
        String SAME_BPN = "가게명";
        ReflectionTestUtils.setField(restaurant1, "roadAddress", SAME_ADDRESS);
        ReflectionTestUtils.setField(restaurant1, "businessPlaceName", SAME_BPN);
        ReflectionTestUtils.setField(restaurant2, "roadAddress", SAME_ADDRESS);
        ReflectionTestUtils.setField(restaurant2, "businessPlaceName", SAME_BPN);

        // Mock rawRestaurantRepository.save() 메서드를 통해 저장 시도를 테스트
        when(restaurantRepository.save(restaurant1)).thenReturn(restaurant1);
        when(restaurantRepository.save(restaurant2)).thenThrow(DataIntegrityViolationException.class); // 중복 저장시 예외

        //when
        List<Restaurant> savedRestaurants = ReflectionTestUtils.invokeMethod(restaurantWriter, "saveAll", restaurantList);
        //then
        verify(restaurantRepository, times(2)).save(any()); // 저장 메서드가 2번 호출되어야 함
        assertThat(savedRestaurants.get(0)).isEqualTo(restaurant1); // 저장된 레스토랑은 raw1과 같아야 함
        assertThat(savedRestaurants.size()).isEqualTo(1); // 저장된 레스토랑 개수는 1개여야 함
    }

}