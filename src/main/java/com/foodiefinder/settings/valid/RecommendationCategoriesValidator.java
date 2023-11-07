package com.foodiefinder.settings.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationCategoriesValidator implements
    ConstraintValidator<ValidRecommendationCategories, String> {

    // 유효한 카테고리 목록
    private static final List<String> validCategories = Arrays.asList("김밥(도시락)", "카페", "이동조리",
        "중국식", "일식", "탕류", "패스트푸드", "생선회", "뷔페식", "복어취급", "정종·대포집(선술집)", "전통찻집", "출장조리");

    /***
     * 유효한 카테고리만 필터링 후 String으로 만들어서 반환
     */
    public static String filterValidCategories(String value) {
        if (value == null) {
            return ""; // 값이 null이면 공백 리턴
        }

        // 입력된 카테고리 목록
        List<String> inputCategories = Arrays.stream(value.split(",")).map(String::trim).toList();

        // 유효한 카테고리와 일치하는 것들 필터링
        List<String> matchingCategories = inputCategories.stream()
            .filter(validCategories::contains)
            .collect(Collectors.toList());

        // 결과를 String으로 변환해서 반환
        return String.join(", ", matchingCategories);
    }

    @Override
    public void initialize(ValidRecommendationCategories constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // 값이 null이면 검증 실패
        }

        // 입력된 카테고리 목록
        List<String> inputCategories = Arrays.stream(value.split(",")).map(String::trim).toList();

        // 입력 카테고리 중에서 최소 1개 이상의 유효한 카테고리가 포함되는지 확인
        return inputCategories.stream().anyMatch(validCategories::contains);

    }

}
