package com.foodiefinder.user.service;

import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.user.crypto.PasswordEncoder;
import com.foodiefinder.user.dto.UserDetailResponse;
import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원가입 테스트")
    @Test
    void userSignup() throws Exception {

        User user = User
                .builder()
                .account("testAccount")
                .password(passwordEncoder.encrypt("password123!@#"))
                .build();

        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("password123!@#")
                .build();

        //given
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        userService.saveUser(request);

        //then
        verify(userRepository, times(1)).save(any(User.class));

    }

    @DisplayName("회원가입 계정중복 테스트")
    @Test
    void userSignupCheckAccount() throws Exception {

        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("password123!@#")
                .build();

        //given
        given(userRepository.findByAccount(any())).willThrow(new CustomException(ErrorCode.USER_ALREADY_EXIST));

        //when
        assertThrows(CustomException.class, () -> userService.saveUser(request));

        //then
        verify(userRepository, times(0)).save(any(User.class));

    }

    @DisplayName("회원 상세 조회")
    @Test
    void userDetail() throws Exception {

        User user = User.builder()
                .account("testAccount")
                .password("123123qwe!!")
                .build();

        userRepository.save(user);

        Long userId = user.getId();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserDetailResponse response = userService.getUserDetail(userId);

        assertEquals(userId, response.getId());
        assertEquals(user.getAccount(), response.getAccount());
        assertEquals(user.getLatitude(), response.getLatitude());
        assertEquals(user.getLongitude(), response.getLongitude());
        assertEquals(user.isLunchRecommendationEnabled(), response.isLunchRecommendationEnabled());
    }


}