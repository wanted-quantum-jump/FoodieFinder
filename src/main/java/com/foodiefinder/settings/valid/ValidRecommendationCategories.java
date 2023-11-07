package com.foodiefinder.settings.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RecommendationCategoriesValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRecommendationCategories {
    String message() default "유효하지 않은 카테고리입니다."; // 사용자 정의 메시지 추가
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
