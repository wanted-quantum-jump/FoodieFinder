package com.foodiefinder.restaurant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.foodiefinder.common.dto.ResponseDto;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
import com.foodiefinder.datapipeline.writer.repository.SggRepository;
import com.foodiefinder.restaurant.service.CityService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SggTest {
    @Mock
    private SggRepository sggRepository;

    @InjectMocks
    private CityService sggService;

    @Test
    public void 시군구_목록을_가져온다() {
        // Arrange
        Sgg sgg1 = new Sgg("dosi1", "sgg1", 10.0, 20.0);
        Sgg sgg2 = new Sgg("dosi2", "sgg2", 30.0, 40.0);
        List<Sgg> getCities = Arrays.asList(sgg1, sgg2);

        ResponseDto responseDto1 = new ResponseDto(sgg1);
        ResponseDto responseDto2 = new ResponseDto(sgg2);
        List<ResponseDto> expectedResponse = Arrays.asList(responseDto1, responseDto2);

        when(sggRepository.findAll()).thenReturn(getCities);

        // Act
        List<ResponseDto> actualResponse = sggService.getCities();

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void 시군구_데이터가_비어있을때_NULL_을_반환해야한다() {
        // Arrange
        when(sggRepository.findAll()).thenReturn(null);
        List<ResponseDto> expectedResponse = Collections.emptyList();

        // Act
        List<ResponseDto> actualResponse = sggService.getCities();

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }
}
