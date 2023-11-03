package com.foodiefinder.cities.service;


import com.foodiefinder.cities.controller.response.CitesResponses;
import com.foodiefinder.cities.domain.SggList;
import com.foodiefinder.cities.factory.SggListFactory;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.foodiefinder.datapipeline.writer.repository.SggRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CityService {
    private final SggRepository sggRepository;
    private final SggListFactory sggListFactory;

    public Response<CitesResponses> getCities() {
        List<Sgg> rawCitiesData = getRawCities();
        SggList rawCities = sggListFactory.createSggList(rawCitiesData);
        CitesResponses citesResponses = rawCities.toCitesResponses();
        return Response.success(citesResponses);
    }

    private List<Sgg> getRawCities() {
        return Optional.of(sggRepository.findAll())
                .orElseThrow(() -> new CustomException(ErrorCode.CITIES_DATA_NOT_FOUND));
    }
}
