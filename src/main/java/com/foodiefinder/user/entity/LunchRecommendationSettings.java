package com.foodiefinder.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LunchRecommendationSettings {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean lunchRecommendationEnabled;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;



    @Builder
    public LunchRecommendationSettings(boolean lunchRecommendationEnabled, User user) {
        this.lunchRecommendationEnabled = lunchRecommendationEnabled;
        this.user = user;
    }

    public void infoUpdate(boolean lunchRecommendationEnabled) {
        this.lunchRecommendationEnabled = lunchRecommendationEnabled != false ?
                lunchRecommendationEnabled : this.lunchRecommendationEnabled;
    }


}
