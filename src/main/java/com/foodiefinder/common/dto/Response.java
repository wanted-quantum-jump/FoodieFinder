package com.foodiefinder.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    private int status;
    private String message;
    private T data;

    private ResponseDto(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(HttpStatus.OK, "Success", data);
    }

}
