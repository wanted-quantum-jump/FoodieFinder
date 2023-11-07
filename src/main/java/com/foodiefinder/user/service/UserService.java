package com.foodiefinder.user.service;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.user.crypto.PasswordEncoder;
import com.foodiefinder.user.dto.UserDetailResponse;
import com.foodiefinder.user.dto.UserInfoUpdateRequest;
import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.entity.LunchRecommendationSettings;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.LunchRecommendationSettingsRepository;
import com.foodiefinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LunchRecommendationSettingsRepository lunchRecommendationSettingsRepository;

    @Transactional
    public Long saveUser(UserSignupRequest request) {

        checkDuplicateAccount(request.getAccount());

        String encryptedPassword = passwordEncoder.encrypt(request.getPassword());

        User user = User.builder()
                .account(request.getAccount())
                .password(encryptedPassword)
                .build();

        LunchRecommendationSettings lunchRecommendationSettings = LunchRecommendationSettings.builder()
                .user(user)
                .lunchRecommendationEnabled(false)
                .build();

        User savedUser = userRepository.save(user);
        lunchRecommendationSettingsRepository.save(lunchRecommendationSettings);

        return savedUser.getId();

    }

    private void checkDuplicateAccount(String account) {
        userRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
                });
    }

    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LunchRecommendationSettings lunchRecommendationEnabled = lunchRecommendationSettingsRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserDetailResponse response = UserDetailResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .lunchRecommendationEnabled(lunchRecommendationEnabled.isLunchRecommendationEnabled())
                .build();

        return response;
    }

    @Transactional
    public void infoUpdate(Long userId, UserInfoUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LunchRecommendationSettings lunchRecommendationEnabled = lunchRecommendationSettingsRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.userInfoUpdate(request.getLatitude(), request.getLongitude());

        lunchRecommendationEnabled.infoUpdate(request.isLunchRecommendationEnabled());

    }
}
