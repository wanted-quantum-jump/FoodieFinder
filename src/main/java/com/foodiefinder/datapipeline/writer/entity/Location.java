package com.foodiefinder.datapipeline.writer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * WGS84위도 : REFINE_WGS84_LAT
     */
    private Double latitude;

    /**
     * WGS84경도 : REFINE_WGS84_LOGT
     */
    private Double longitude;

    @JsonIgnore
    @OneToOne(mappedBy = "location", fetch = LAZY)
    private Restaurant restaurant;

    //== 생성 메서드 == //
    @Builder
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //=== 연관관계 메서드 ===//
    protected void setLocation(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

}