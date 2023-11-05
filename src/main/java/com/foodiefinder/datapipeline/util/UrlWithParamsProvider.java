package com.foodiefinder.datapipeline.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class UrlWithParamsProvider implements UrlProvider<String> {

    /**
     * 파라미터로 url 을 만들어 반환
     * @param url - 요청 보낼 url
     * @param paramsMap - 요청 보낼 때 사용할 파라미터 key/value
     * @return param 을 넣은 url 완성
     */
    @Override
    public String getUrl(String url, Map<String, String> paramsMap) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
        }

        return uriComponentsBuilder.toUriString();
    }
}
