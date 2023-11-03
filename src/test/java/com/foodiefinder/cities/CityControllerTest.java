package com.foodiefinder.cities;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.foodiefinder.cities.controller.response.CitesResponse;
import com.foodiefinder.cities.controller.response.CitesResponses;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.cities.controller.CityController;
import com.foodiefinder.cities.service.CityService;
import com.foodiefinder.datapipeline.writer.entity.Sgg;
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
    public void 시군구_목록을_반환한다() throws Exception {
        List<CitesResponse> citesResponseList = Arrays.asList(
                CitesResponse.fromSgg(new Sgg("Dosi1", "Sgg1", 37.5665, 126.9780)),
                CitesResponse.fromSgg(new Sgg("Dosi2", "Sgg2", 35.1796, 129.0756))
        );
        CitesResponses citesResponses = new CitesResponses(citesResponseList);
        Response<CitesResponses> expectedResponse = Response.success(citesResponses);
        given(sggService.getCities()).willReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/api/cities")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andExpect(jsonPath("$.data.citesResponses", hasSize(citesResponseList.size())))
                .andExpect(jsonPath("$.data.citesResponses[0].dosi").value(citesResponseList.get(0).getDosi()))
                .andExpect(jsonPath("$.data.citesResponses[0].sgg").value(citesResponseList.get(0).getSgg()))
                .andExpect(jsonPath("$.data.citesResponses[0].lon").value(citesResponseList.get(0).getLon()))
                .andExpect(jsonPath("$.data.citesResponses[0].lat").value(citesResponseList.get(0).getLat()))
                .andExpect(jsonPath("$.data.citesResponses[1].dosi").value(citesResponseList.get(1).getDosi()))
                .andExpect(jsonPath("$.data.citesResponses[1].sgg").value(citesResponseList.get(1).getSgg()))
                .andExpect(jsonPath("$.data.citesResponses[1].lon").value(citesResponseList.get(1).getLon()))
                .andExpect(jsonPath("$.data.citesResponses[1].lat").value(citesResponseList.get(1).getLat()));
    }
}