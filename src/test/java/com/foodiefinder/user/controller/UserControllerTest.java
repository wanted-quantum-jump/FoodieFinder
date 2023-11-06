package com.foodiefinder.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.auth.config.SecurityConfig;
import com.foodiefinder.auth.filter.JwtAuthenticationFilter;
import com.foodiefinder.auth.jwt.JwtUtils;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.repository.UserRepository;
import com.foodiefinder.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean //관련 bean 목객체로
    private UserRepository userRepository;


    @DisplayName("회원가입 성공")
    @WithMockUser
    @Test
    void userSignup() throws Exception {
        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        given(userService.saveUser(any())).willReturn(1L);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/users/signup").with(csrf())
                .content(json)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService).saveUser(any());
    }

    @DisplayName("회원가입 실패 - 계정 중복")
    @WithMockUser
    @Test
    void userSignupFail() throws Exception {

        UserSignupRequest request = UserSignupRequest.builder()
                .account("testAccount")
                .password("test1234!@#$%")
                .build();

        given(userService.saveUser(any())).willThrow(new CustomException(ErrorCode.USER_ALREADY_EXIST));

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/users/signup").with(csrf())
                .content(json)
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService).saveUser(any());
    }






}