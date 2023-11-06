package com.foodiefinder.user.controller;


import com.foodiefinder.user.dto.UserDetailResponse;
import com.foodiefinder.user.dto.UserInfoUpdateRequest;
import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> userDetail(@PathVariable Long userId) {

        UserDetailResponse response = userService.getUserDetail(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> userInfoUpdate(@PathVariable Long userId,
                                            @RequestBody UserInfoUpdateRequest request) {

        userService.infoUpdate(userId, request);

        return ResponseEntity.noContent().build();
    }
}

