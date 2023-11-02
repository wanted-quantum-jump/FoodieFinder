package com.foodiefinder.restaurant.controller;

import com.foodiefinder.common.dto.ResponseDto;
import com.foodiefinder.restaurant.service.CityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/citys")
@RequiredArgsConstructor
public class CityController {
    private final CityService sggService;

    @GetMapping
    public List<ResponseDto> getCities() {
        return this.sggService.getCities();
    }
}
