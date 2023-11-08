package com.foodiefinder.auth.service;

import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.auth.jwt.JwtUtils;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.user.crypto.PasswordEncoder;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @DisplayName("로그인 성공")
    @Test
    void login_Success() {
        // Arrange
        String account = "testAccount";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String[] tokens = new String[]{"access-token", "refresh-token"};

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        User user = User.builder()
                .account(account)
                .password(encodedPassword)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateToken(account)).thenReturn(tokens);

        //when
        String[] result = authService.login(request);

        //then
        assertEquals(tokens[0], result[0]);
        assertEquals(tokens[1], result[1]);
    }

    @DisplayName("로그인 시 유저 못찾음")
    @Test
    void login_UserNotFound() {
        String account = "testAccount";
        String password = "password123";

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.empty());

        //then
        assertThrows(CustomException.class, () -> authService.login(request));
    }

    @DisplayName("로그인시 패스워드 일치하지 않음")
    @Test
    void login_PasswordMismatch() {
        String account = "testAccount";
        String password = "password123";
        String encodedPassword = "encodedPassword123";

        UserLoginRequest request = UserLoginRequest.builder()
                .account(account)
                .password(password)
                .build();

        User user = User.builder()
                .account(account)
                .password(encodedPassword)
                .build();

        //given
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        //then
        assertThrows(CustomException.class, () -> authService.login(request));
    }
}