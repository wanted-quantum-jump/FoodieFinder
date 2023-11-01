package com.foodiefinder.datapipeline.processor;

import com.foodiefinder.datapipeline.processor.dto.RootData;
import com.foodiefinder.datapipeline.writer.entity.RawRestaurant;

import java.util.ArrayList;
import java.util.List;

import static com.foodiefinder.datapipeline.processor.dto.RootData.*;

public class RawRestaurantProcessor implements ItemProcessor<String, List<RawRestaurant>> {

    @Override
    public List<RawRestaurant> process(String jsonString) {
        RootData rootData = of(jsonString);

        //헤더
        String apiVersion = rootData.getApiVersion();
        int listTotalCount = rootData.getListTotalCount();
        ResultElement resultElement = rootData.getResultElement();

        //row 배열
        List<RawRestaurant> rawRestaurantList = new ArrayList<>();
        for (int idx = 0; idx < rootData.getRowElements().size(); idx++) {
            RowData rowData = rootData.getRowElements().get(idx);
            RawRestaurant rawRestaurant = getRawRestaurant(apiVersion, listTotalCount, resultElement, rowData);
            rawRestaurantList.add(rawRestaurant);
        }
        return rawRestaurantList;
    }

    private RawRestaurant getRawRestaurant(String apiVersion, int listTotalCount, ResultElement resultElement, RowData rowData) {
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
        return rawRestaurant;
    }
}