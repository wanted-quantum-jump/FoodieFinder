package com.foodiefinder.restaurant.service;

import com.foodiefinder.common.dto.ResponseDto;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.foodiefinder.datapipeline.writer.repository.SggRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public List<ResponseDto> getCities() {
        List<Sgg> getCities = getRawCities();

        return getCities.stream()
                .map(ResponseDto::new)
                .collect(Collectors.toList());
    }

    private List<Sgg> getRawCities() {
        return Optional.ofNullable(sggRepository.findAll())
                .orElse(Collections.emptyList());
    }
}
