package com.foodiefinder.restaurants.entity;

import com.foodiefinder.datapipeline.writer.entity.Restaurant;
import com.foodiefinder.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    private int value;
    private String comment;

    @Builder
    public Rating(User user, Restaurant restaurant, int value, String comment) {
        this.user = user;
        this.restaurant = restaurant;
        this.value = value;
        this.comment = comment;
    }

}
