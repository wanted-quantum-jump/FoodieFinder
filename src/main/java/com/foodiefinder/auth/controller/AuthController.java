package com.foodiefinder.auth.controller;

import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok().body(token);
    }

}
