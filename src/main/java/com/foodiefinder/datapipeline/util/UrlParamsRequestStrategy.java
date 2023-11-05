package com.foodiefinder.datapipeline.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class UrlParamsRequestStrategy<T> implements RequestStrategy<T, Map<String, String>> {

    private final RestTemplate restTemplate = new RestTemplate();

    private final UrlProvider<String> urlProvider = new UrlWithParamsProvider();

    // Url 로 요청
    public ResponseEntity<T> sendRequest(String url, Map<String,String> params, Class<T> clazz) {
        return restTemplate.getForEntity(urlProvider.getUrl(url, params), clazz);
    }
}