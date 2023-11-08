package com.foodiefinder.datapipeline.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foodiefinder.datapipeline.processor.dto.RootData;
import com.foodiefinder.datapipeline.processor.dto.RootData.ResultElement;
import com.foodiefinder.datapipeline.processor.dto.RootData.RowData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

// TODO: 모든 필드가 정확한 위치에 맵핑되는지 테스트

@Slf4j
@DisplayName("단위테스트 - RawRestaurantProcessorTest")
class RawRestaurantProcessorTest {

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
    @DisplayName("json 내의 row 배열 읽기")
    void process() throws JsonProcessingException {

        RootData rootData = RootData.of(jsonString);
        //when
        // RootData 객체에서 원하는 데이터 추출
        List<RowData> rowDataList = rootData.getRowElements(); //row

        //than
        RowData firstRowData = rowDataList.get(0);
        Assertions.assertThat(firstRowData.getSigunName()).isEqualTo("용인시 ");
        Assertions.assertThat(firstRowData.getSigunCode()).isEqualTo(null);
        Assertions.assertThat(firstRowData.getLicenseDate()).isEqualTo("20091204");

    }

    @Test
    @DisplayName("json 내의 헤드 읽기 - version")
    void processHeadForVersion() throws JsonProcessingException {

        RootData rootData = RootData.of(jsonString);
        //when
        // RootData 객체에서 원하는 데이터 추출
        String version = rootData.getApiVersion(); //version
        //than
        Assertions.assertThat(version).isEqualTo("1.0");
    }

    @Test
    @DisplayName("json 내의 헤드 읽기 - listTotalCount")
    void processHeadForListTotalCount() throws JsonProcessingException {

        RootData rootData = RootData.of(jsonString);
        //when
        // RootData 객체에서 원하는 데이터 추출
        String listTotalCount = rootData.getListTotalCount(); //listTotalCount
        //than
        Assertions.assertThat(listTotalCount).isEqualTo("4752");
    }

    @Test
    @DisplayName("json 내의 헤드 읽기 - ResultElement")
    void processHeadForListResultElement() throws JsonProcessingException {

        RootData rootData = RootData.of(jsonString);
        //when
        // RootData 객체에서 원하는 데이터 추출
        ResultElement resultElement = rootData.getResultElement(); //ResultElement
        //than
        Assertions.assertThat(resultElement.getCODE()).isEqualTo("INFO-000");
        Assertions.assertThat(resultElement.getMESSAGE()).isEqualTo("정상 처리되었습니다.");
    }

    @Test
    @DisplayName("json to Entity")
    void jsonToEntity() {

        //given
        RootData rootData = RootData.of(jsonString);

        //when
        //헤더
        String apiVersion = rootData.getApiVersion();
        String listTotalCount = rootData.getListTotalCount();
        ResultElement resultElement = rootData.getResultElement();

        //row 배열
        List<RawRestaurant> list = new ArrayList<>();
        for (int idx = 0; idx < rootData.getRowElements().size(); idx++) {
            RowData rowData = rootData.getRowElements().get(idx);
            //when
            RawRestaurant rawRestaurant = RawRestaurant.builder()
                    .listTotalCount(listTotalCount)
                    .code(resultElement.getCODE())
                    .message(resultElement.getMESSAGE())
                    .apiVersion(apiVersion)
                    .sigunName(rowData.getSigunName())
                    .sigunCode(rowData.getSigunCode())
                    .businessPlaceName(rowData.getBusinessPlaceName())
                    .licenseDate(rowData.getLicenseDate())
                    .businessStateName(rowData.getBusinessStateName())
                    .closeDate(rowData.getCloseDate())
                    .locationArea(rowData.getLocationArea())
                    .waterFacilityTypeName(rowData.getWaterFacilityTypeName())
                    .maleEmployeeCount(rowData.getMaleEmployeeCount())
                    .year(rowData.getYear())
                    .multiUseBusinessEstablishment(rowData.getMultiUseBusinessEstablishment())
                    .gradeDivisionName(rowData.getGradeDivisionName())
                    .totalFacilityScale(rowData.getTotalFacilityScale())
                    .femaleEmployeeCount(rowData.getFemaleEmployeeCount())
                    .businessSiteCircumferenceTypeName(rowData.getBusinessSiteCircumferenceTypeName())
                    .sanitationIndustryType(rowData.getSanitationIndustryType())
                    .sanitationBusinessCondition(rowData.getSanitationBusinessCondition())
                    .totalEmployeeCount(rowData.getTotalEmployeeCount())
                    .roadAddress(rowData.getRoadAddress())
                    .lotNumberAddress(rowData.getLotNumberAddress())
                    .zipCode(rowData.getZipCode())
                    .latitude(rowData.getLatitude())
                    .longitude(rowData.getLongitude())
                    .build();
            list.add(rawRestaurant);
        }
        //than
        Assertions.assertThat(list.size()).isEqualTo(rootData.dataElements.size());

    }


}