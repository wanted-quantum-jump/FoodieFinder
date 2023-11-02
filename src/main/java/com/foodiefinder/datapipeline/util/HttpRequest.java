package com.foodiefinder.datapipeline.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpRequest<T> {
    
    // Url 로 요청
    public ResponseEntity<T> sendRequest(String url, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, clazz);
    }
}