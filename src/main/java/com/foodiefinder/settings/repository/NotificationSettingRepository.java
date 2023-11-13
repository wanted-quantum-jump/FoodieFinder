package com.foodiefinder.settings.repository;

import com.foodiefinder.settings.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    List<NotificationSetting> findAllByIsLunchRecommendationAllowed(Boolean b);

    Optional<NotificationSetting> findByUserId(Long userId);
}
