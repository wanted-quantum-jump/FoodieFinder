package com.foodiefinder.auth.controller;

import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {

        String[] tokens = authService.login(request);

        Map<String, String> response = new HashMap<>();
      
        response.put("Bearer", tokens[0]);

        ResponseCookie refreshToken = ResponseCookie.from("Bearer", tokens[1])
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .secure(true)
                .build();

        return ResponseEntity.ok().header("Set-Cookie", refreshToken.toString()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("Bearer") String refreshToken) {
        String newAccessToken = authService.issueRefreshToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("Bearer", newAccessToken);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(Authentication authentication) {
        return ResponseEntity.ok().body(authentication.getName());
    }

}
