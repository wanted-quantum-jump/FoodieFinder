package com.foodiefinder.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.auth.filter.JwtAuthenticationFilter;
import com.foodiefinder.auth.jwt.JwtUtils;
import com.foodiefinder.auth.service.AuthService;
import com.foodiefinder.user.controller.UserController;
import com.foodiefinder.user.dto.UserSignupRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtils.class)
        })
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;


    @DisplayName("로그인 테스트")
    @WithMockUser
    @Test
    void login() throws Exception {

        UserLoginRequest request = UserLoginRequest.builder()
                .account("testAccount")
                .password("password123!!")
                .build();

        String[] returnData = new String[]{"access-token", "refresh-token"};

        given(authService.login(any(UserLoginRequest.class))).willReturn(returnData);

        mockMvc.perform(post("/api/users/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"Bearer\":\"access-token\"}")) //응답 액세스토큰
                .andExpect(cookie().value("Bearer", "refresh-token")); //쿠키 리프레시토큰
    }

    @DisplayName("인증, 인가가 필요한 URL 테스트")
    @WithMockUser//권한이 있는 유저 생성
    @Test
    void authority() throws Exception {

        mockMvc.perform(get("/api/users/test")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("user"));

    }

    @DisplayName("새로운 액세스 토큰 발급")
    @WithMockUser
    @Test
    void issueRefreshToken() throws Exception{

        String refreshToken = "refresh-token";
        String newAccessToken = "new-access-token";

        given(authService.issueRefreshToken(refreshToken)).willReturn(newAccessToken);

        mockMvc.perform(post("/api/users/refresh").with(csrf())
                    .cookie(new Cookie("Bearer", refreshToken)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"Bearer\":\"new-access-token\"}"));
    }

}