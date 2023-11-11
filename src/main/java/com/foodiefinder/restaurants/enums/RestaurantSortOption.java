package com.foodiefinder.restaurants.enums;

public enum RestaurantSortOption {
    RATING("rating"), DISTANCE("distance"), REVIEW_COUNT("review");

    private final String option;

    RestaurantSortOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
