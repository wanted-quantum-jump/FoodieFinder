package com.foodiefinder.datapipeline.util.response;

import org.springframework.http.ResponseEntity;

/**
 * 요청을 보내는 클래스의 인터페이스
 * 
 * @param <T> 응답 매핑 타입
 * @param <P> 전송 요청 타입, 파라미터를 Url 에 포함하면 Map<K,V> 와 같이, Json 으로 Body 에 포함하면 해당 클래스.
 */
public interface RequestStrategy<T, P> {
    ResponseEntity<T> sendRequest(String url, P requestDetails, Class<T> responseType);
}
