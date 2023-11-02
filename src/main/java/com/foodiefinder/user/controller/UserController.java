package com.foodiefinder.user.controller;


import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> joinUser(@Valid @RequestBody UserSignupRequest request) {

        Long userId = userService.saveUser(request);

        return ResponseEntity.created(URI.create("/api/users/signup/" + userId)).build();
    }
}

