package com.foodiefinder.user.service;

import com.foodiefinder.user.crypto.PasswordEncoder;
import com.foodiefinder.user.dto.UserSignupRequest;
import com.foodiefinder.user.entity.User;
import com.foodiefinder.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long saveUser(UserSignupRequest request) {

        //todo 계정의 유니크 여부로 예외처리하기

        String encryptedPassword = passwordEncoder.encrypt(request.getPassword());

        User user = User.builder()
                .account(request.getAccount())
                .password(encryptedPassword)
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
