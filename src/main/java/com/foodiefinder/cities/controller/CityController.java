package com.foodiefinder.cities.controller;

import com.foodiefinder.cities.controller.response.CitesResponse;
import com.foodiefinder.cities.controller.response.CitesResponses;
import com.foodiefinder.common.dto.Response;
import com.foodiefinder.cities.service.CityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService sggService;

    @GetMapping
    public Response<CitesResponses> getCities() {
        return sggService.getCities();
    }
}
