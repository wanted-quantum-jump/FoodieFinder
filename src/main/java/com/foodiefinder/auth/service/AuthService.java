package com.foodiefinder.auth.service;

import com.foodiefinder.auth.jwt.JwtUtils;
import com.foodiefinder.auth.dto.UserLoginRequest;
import com.foodiefinder.common.exception.CustomException;
import com.foodiefinder.common.exception.ErrorCode;
import com.foodiefinder.user.crypto.PasswordEncoder;
import com.foodiefinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String[] login(UserLoginRequest request) {

        //중첩 람다 -> db 조회 1번
        userRepository.findByAccount(request.getAccount()).
                filter(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return true;
                    } else {
                        throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
                    }
                })
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return jwtUtils.generateToken(request.getAccount());
    }

    public String issueRefreshToken(String refreshToken) {

        return jwtUtils.verifyRefreshTokenAndReissue(refreshToken);
    }
}
