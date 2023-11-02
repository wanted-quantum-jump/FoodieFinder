package com.foodiefinder.datapipeline.processor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RootData {
    //todo: 접근제어자 수정
    @JsonProperty("Genrestrtcate")
    public List<DataElement> dataElements;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static RootData of(String jsonString) {
        try {
            // 원본 JSON 데이터를 RootData 객체로 변환
            return objectMapper.readValue(jsonString, RootData.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.UNPARSEABLE_DATA);
        }
    }

    public String getListTotalCount() {
        return this.dataElements.get(0).getHeadElements().get(0).getListTotalCount();
    }

    public ResultElement getResultElement() {
        return this.dataElements.get(0).getHeadElements().get(1).getResultElement();
    }

    public String getApiVersion() {
        return this.dataElements.get(0).getHeadElements().get(2).getApiVersion();
    }

    public List<RowData> getRowElements() {
        return this.dataElements.get(1).getRowElements();
    }

    // "Genrestrtcate" 배열의 요소 클래스
    @Getter
    public static class DataElement {
        @JsonAlias("head")
        private List<HeadElement> headElements;

        @JsonAlias("row")
        private List<RowData> rowElements;
    }

    // "head" 배열의 요소 클래스
    @Getter
    public static class HeadElement {
        @JsonProperty("list_total_count")
        private String listTotalCount;
        @JsonProperty("RESULT")
        private ResultElement resultElement;
        @JsonProperty("api_version")
        private String apiVersion;

        @Override
        public String toString() {
            return "HeadElement{" +
                    "listTotalCount=" + listTotalCount +
                    ", resultElement=" + resultElement +
                    ", apiVersion='" + apiVersion + '\'' +
                    '}';
        }

    }

    // "RESULT" 객체 클래스
    @Getter
    public static class ResultElement {
        @JsonProperty("CODE")
        private String CODE;
        @JsonProperty("MESSAGE")
        private String MESSAGE;
    }

    // "row" 배열을 나타내는 클래스
    @Getter
    public static class RowData {
        @JsonProperty("SIGUN_NM")
        private String sigunName;
        @JsonProperty("SIGUN_CD")
        private String sigunCode;
        @JsonProperty("BIZPLC_NM")
        private String businessPlaceName;
        @JsonProperty("LICENSG_DE")
        private String licenseDate;
        @JsonProperty("BSN_STATE_NM")
        private String businessStateName;
        @JsonProperty("CLSBIZ_DE")
        private String closeDate;
        @JsonProperty("LOCPLC_AR")
        private String locationArea;
        @JsonProperty("GRAD_FACLT_DIV_NM")
        private String waterFacilityTypeName;
        @JsonProperty("MALE_ENFLPSN_CNT")
        private String maleEmployeeCount;
        @JsonProperty("YY")
        private String year;
        @JsonProperty("MULTI_USE_BIZESTBL_YN")
        private String multiUseBusinessEstablishment;
        @JsonProperty("GRAD_DIV_NM")
        private String gradeDivisionName;
        @JsonProperty("TOT_FACLT_SCALE")
        private String totalFacilityScale;
        @JsonProperty("FEMALE_ENFLPSN_CNT")
        private String femaleEmployeeCount;
        @JsonProperty("BSNSITE_CIRCUMFR_DIV_NM")
        private String businessSiteCircumferenceTypeName;
        @JsonProperty("SANITTN_INDUTYPE_NM")
        private String sanitationIndustryType;
        @JsonProperty("SANITTN_BIZCOND_NM")
        private String sanitationBusinessCondition;
        @JsonProperty("TOT_EMPLY_CNT")
        private String totalEmployeeCount;
        @JsonProperty("REFINE_LOTNO_ADDR")
        private String lotNumberAddress;
        @JsonProperty("REFINE_ROADNM_ADDR")
        private String roadAddress;
        @JsonProperty("REFINE_ZIP_CD")
        private String zipCode;
        @JsonProperty("REFINE_WGS84_LOGT")
        private String longitude;
        @JsonProperty("REFINE_WGS84_LAT")
        private String latitude;

        @Override
        public String toString() {
            return "RowData{" +
                    "sigunName='" + sigunName + '\'' +
                    ", sigunCode='" + sigunCode + '\'' +
                    ", businessPlaceName='" + businessPlaceName + '\'' +
                    ", licenseDate='" + licenseDate + '\'' +
                    ", businessStateName='" + businessStateName + '\'' +
                    ", closeDate='" + closeDate + '\'' +
                    ", locationArea=" + locationArea +
                    ", waterFacilityTypeName='" + waterFacilityTypeName + '\'' +
                    ", maleEmployeeCount=" + maleEmployeeCount +
                    ", year=" + year +
                    ", multiUseBusinessEstablishment='" + multiUseBusinessEstablishment + '\'' +
                    ", gradeDivisionName='" + gradeDivisionName + '\'' +
                    ", totalFacilityScale=" + totalFacilityScale +
                    ", femaleEmployeeCount=" + femaleEmployeeCount +
                    ", businessSiteCircumferenceTypeName='" + businessSiteCircumferenceTypeName + '\'' +
                    ", sanitationIndustryType='" + sanitationIndustryType + '\'' +
                    ", sanitationBusinessCondition='" + sanitationBusinessCondition + '\'' +
                    ", totalEmployeeCount=" + totalEmployeeCount +
                    ", lotNumberAddress='" + lotNumberAddress + '\'' +
                    ", roadAddress='" + roadAddress + '\'' +
                    ", zipCode=" + zipCode +
                    ", longitude=" + longitude +
                    ", latitude=" + latitude +
                    '}';
        }
    }

    public List<RawRestaurant> toEntityList() {
        String apiVersion = this.getApiVersion();
        String listTotalCount = this.getListTotalCount();
        ResultElement resultElement = this.getResultElement();

        //row 배열
        List<RawRestaurant> list = new ArrayList<>();
        for (int idx = 0; idx < this.getRowElements().size(); idx++) {
            RowData rowData = this.getRowElements().get(idx);
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
        return list;
    }

}