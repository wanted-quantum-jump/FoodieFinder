package com.foodiefinder.datapipeline.writer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class RestaurantTest {

    @Test
    @DisplayName("양방향 연관관계를 갖는 Restaurant -> Json 변환 테스트 - 성공")
    void toJson() {

        //given
        Restaurant restaurant = Restaurant
                .builder()
                .code(10025)
                .address(Address.builder().lotNumberAddress("지번주소").roadAddress("도로명주소").build())
                .location(Location.builder().latitude(99.999D).longitude(8.876D).build())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        //when
        //성공 : 에러가 발생하지 않았음
        //실패 : 직렬화 실패로 JsonProcessingException 예외 발생
        Assertions.assertDoesNotThrow(() -> mapper.writeValueAsString(restaurant));

    }

}