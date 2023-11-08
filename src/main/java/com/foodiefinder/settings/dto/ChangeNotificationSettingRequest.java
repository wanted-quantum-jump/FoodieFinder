package com.foodiefinder.settings.dto;

import com.foodiefinder.settings.entity.NotificationSetting;
import com.foodiefinder.settings.valid.RecommendationCategoriesValidator;
import com.foodiefinder.settings.valid.ValidRecommendationCategories;
import com.foodiefinder.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeNotificationSettingRequest {

    @NotNull(message =  "유저 id는 null일 수 없습니다.")
    Long userId;

    Boolean isLunchRecommendationAllowed = Boolean.FALSE;

    @ValidRecommendationCategories
    String recommendationCategories;

    @NotBlank(message = "webHookUrl은 필수 값입니다.")
    String webHookUrl;

    @Min(value = 100,  message = "최소 100미터 이상이어야 합니다.")
    @Max(value = 10000,  message = "최대 10000미터 이하이어야 합니다.")
    Integer recommendationRange = 1000;


    @Builder
    public NotificationSetting toEntity(User user) {
        return NotificationSetting.builder()
                .user(user)
                .isLunchRecommendationAllowed(isLunchRecommendationAllowed)
                .recommendationCategories(RecommendationCategoriesValidator.filterValidCategories(recommendationCategories)) //유효한 카테고리만 필터링
                .webHookUrl(webHookUrl)
                .recommendationRange(recommendationRange)
                .build();
    }

    /***
     * getter 사용시 유효한 카테고리만 필터링
     * @return 유효한 카테고리들의 String
     */
    public String getRecommendationCategories() {
        return RecommendationCategoriesValidator.filterValidCategories(recommendationCategories);
    }
}
