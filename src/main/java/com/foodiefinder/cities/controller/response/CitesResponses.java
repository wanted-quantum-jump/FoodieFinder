package com.foodiefinder.cities.controller.response;

import java.util.List;
import lombok.Data;

@Data
public class CitesResponses {
    private List<CitesResponse> citesResponses;

    public CitesResponses(List<CitesResponse> citesResponseList) {
        this.citesResponses = citesResponseList;
    }
}
