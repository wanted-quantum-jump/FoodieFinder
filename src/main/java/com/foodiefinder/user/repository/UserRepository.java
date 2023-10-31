package com.foodiefinder.user.repository;

import com.foodiefinder.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
