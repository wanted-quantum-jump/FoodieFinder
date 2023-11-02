package com.foodiefinder.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private int status;
    private String message;
    private Object data;

    public ResponseDto() {
        HttpStatus httpStatus = HttpStatus.NO_CONTENT;
        this.status = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    public ResponseDto(HttpStatus httpStatus, Object data) {
        this.status = httpStatus.value();
        this.data = data;
    }

    public ResponseDto(HttpStatus httpStatus, Exception e) {
        this.status = httpStatus.value();
        this.message = e.getMessage();
    }

    public ResponseDto(Object data) {
        this.status = HttpStatus.OK.value();
        this.data = data;
    }

    public ResponseDto(HttpStatus httpStatus, String message) {
        this.status = httpStatus.value();
        this.message = message;
    }
}
