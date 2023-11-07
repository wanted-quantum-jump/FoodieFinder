package com.foodiefinder.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 예시)
    // RECRUITMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "R001", "해당하는 채용공고가 없습니다."),

    //User
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "U001", "이미 계정이 존재합니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST,"U001" , "존재하지 않는 유저입니다."),

    //Processor
    UNPARSEABLE_DATA(HttpStatus.NOT_IMPLEMENTED, "P001", "Json 형식과 맞지 않는 데이터입니다."),
    MISSING_REQUIRED_VALUE(HttpStatus.NOT_IMPLEMENTED,"P002" , "필수 값이 null입니다."),
    WRONG_NUMBER_FORMAT(HttpStatus.NOT_IMPLEMENTED," P003" , "숫자가 아닌 값입니다."),

    //CSV
    NOT_VALID_CSV(HttpStatus.NOT_IMPLEMENTED,"C001" ,"유효하지 않은 형식의 CSV입니다." ),
    CSV_FILE_EXCEPTION(HttpStatus.NOT_IMPLEMENTED,"C002" ,"CSV 파일을 읽어올 수 없습니다." ),
    NOT_VALID_FILEPATH(HttpStatus.NOT_IMPLEMENTED,"C003" , "파일 경로 혹은 파일에 문제가 있습니다."),
    CITIES_DATA_NOT_FOUND(HttpStatus.NOT_IMPLEMENTED,"C004" , "시군구 데이터가 존재하지 않습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}
