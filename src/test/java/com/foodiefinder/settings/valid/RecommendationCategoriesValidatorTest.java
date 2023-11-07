package com.foodiefinder.settings.valid;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("단위테스트 - RecommendationCategoriesValidator")
class RecommendationCategoriesValidatorTest {

    @DisplayName("검증 통과 : 올바른 입력")
    @Test
    void filterValidCategories() {
        String value = "일식, 중국식, 이동조리";
        String result = RecommendationCategoriesValidator.filterValidCategories(value);
        Assertions.assertThat(result).isEqualTo(value);
    }


    @DisplayName("검증 실패 : 올바르지 않은 카테고리 키워드 포함")
    @Test
    void filterValidCategoriesFail() {
        String value = "읽식, 중국식, 이동조리";
        String result = RecommendationCategoriesValidator.filterValidCategories(value);
        Assertions.assertThat(result).isEqualTo("중국식, 이동조리");
    }
}