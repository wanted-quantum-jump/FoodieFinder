package com.foodiefinder.auth.controller;

import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

        String token = authService.login(request);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(Authentication authentication) {
        return ResponseEntity.ok().body(authentication.getName());
    }

}
