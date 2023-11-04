package com.foodiefinder.cities.domain;

import com.foodiefinder.cities.controller.response.CitesResponse;
import com.foodiefinder.cities.controller.response.CitesResponses;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import java.util.List;
import java.util.stream.Collectors;

public class SggList {
    private final List<Sgg> sggs;

    public SggList(List<Sgg> sggs) {
        this.sggs = sggs;
    }

    public CitesResponses toCitesResponses() {
        return new CitesResponses(sggs.stream()
                .map(CitesResponse::fromSgg)
                .collect(Collectors.toList()));
    }
}
