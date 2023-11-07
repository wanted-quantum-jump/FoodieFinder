package com.foodiefinder.cities.controller.response;

import com.foodiefinder.datapipeline.writer.entity.Sgg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CitesResponse {
    private String dosi;
    private String sgg;
    private Double lon;
    private Double lat;

    public static CitesResponse fromSgg(Sgg sgg) {
        return new CitesResponse(
                sgg.getDosi(),
                sgg.getSgg(),
                sgg.getLon(),
                sgg.getLat()
        );
    }
}
