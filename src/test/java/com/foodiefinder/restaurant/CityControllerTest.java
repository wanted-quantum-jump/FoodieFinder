package com.foodiefinder.restaurant;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.foodiefinder.common.dto.ResponseDto;
import com.foodiefinder.restaurant.controller.CityController;
import com.foodiefinder.restaurant.service.CityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CityController.class)
public class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService sggService;

    @Test
    public void 시군구_목록을_가져온다 () throws Exception {
        // Given
        List<ResponseDto> responseDtos = Arrays.asList(
                new ResponseDto(HttpStatus.OK, "test1"),
                new ResponseDto(HttpStatus.OK, "test2")
        );
        given(sggService.getCities()).willReturn(responseDtos);

        // When & Then
        mockMvc.perform(get("/sgg")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responseDtos.size()))
                .andExpect(jsonPath("$[0].message").value(responseDtos.get(0).getMessage()))
                .andExpect(jsonPath("$[1].message").value(responseDtos.get(1).getMessage()));
    }
}