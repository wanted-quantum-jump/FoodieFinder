package com.foodiefinder.user.repository;

import com.foodiefinder.user.entity.LunchRecommendationSettings;
import com.foodiefinder.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LunchRecommendationSettingsRepository extends JpaRepository<LunchRecommendationSettings, Long> {

    Optional<LunchRecommendationSettings> findByUser(User user);
}
