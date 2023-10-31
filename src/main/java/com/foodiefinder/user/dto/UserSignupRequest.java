package com.foodiefinder.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class UserSignupRequest {

    @NotBlank(message = "계정은 필수 입력값입니다.")
    private String account;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Length(min = 10, message = "비밀번호는 10자 이상이여야합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z!@#$%^&*()-_=+]).+$", message = "비밀번호는 숫자, 문자, 특수문자 중 2가지 이상을 포함해야 합니다.")
    private String password;
}
