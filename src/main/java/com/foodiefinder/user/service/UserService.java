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

        //todo 예외 만들어서 변경하기
        checkDuplicateAccount(request);

        String encryptedPassword = passwordEncoder.encrypt(request.getPassword());

        User user = User.builder()
                .account(request.getAccount())
                .password(encryptedPassword)
                .build();

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    private void checkDuplicateAccount(UserSignupRequest request) {
        userRepository.findByAccount(request.getAccount())
                .ifPresent(user -> {
                    throw new IllegalArgumentException();
                });
    }
}
