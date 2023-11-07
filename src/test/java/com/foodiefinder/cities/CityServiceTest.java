package com.foodiefinder.cities;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.foodiefinder.cities.controller.response.CitesResponse;
import com.foodiefinder.cities.controller.response.CitesResponses;
import com.foodiefinder.cities.factory.SggListFactory;
import com.foodiefinder.cities.service.CityService;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.foodiefinder.datapipeline.writer.repository.SggRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CityService.class, SggListFactory.class})
public class CityServiceTest {

    @MockBean
    private SggRepository sggRepository;

    @Autowired
    private CityService cityService;

    @Test
    public void 도시가_없을_때_빈_목록을_반환한다() {
        // Given
        when(sggRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, cityService::getCities);
        assertEquals(ErrorCode.CITIES_DATA_NOT_FOUND, exception.getErrorCode());

       //Verify
        verify(sggRepository).findAll();
    }
    @Test
    public void 도시가있으면_목록을반환한다() {

        List<Sgg> mockCities = new ArrayList<>();
        mockCities.add(Sgg.builder().dosi("Dosi1").sgg("Sgg1").lon(127.001).lat(37.001).build());
        mockCities.add(Sgg.builder().dosi("Dosi2").sgg("Sgg2").lon(128.002).lat(38.002).build());

        when(sggRepository.findAll()).thenReturn(mockCities);

        Response<CitesResponses> response = cityService.getCities();

        assertNotNull(response);
        assertFalse(response.getData().getCitesResponses().isEmpty());
        assertEquals(mockCities.size(), response.getData().getCitesResponses().size());

        List<CitesResponse> actualCites = response.getData().getCitesResponses();
        for (int i = 0; i < mockCities.size(); i++) {
            Sgg expected = mockCities.get(i);
            CitesResponse actual = actualCites.get(i);
            assertEquals(expected.getDosi(), actual.getDosi());
            assertEquals(expected.getSgg(), actual.getSgg());
            assertEquals(expected.getLon(), actual.getLon());
            assertEquals(expected.getLat(), actual.getLat());
        }

        verify(sggRepository).findAll();
    }
}
