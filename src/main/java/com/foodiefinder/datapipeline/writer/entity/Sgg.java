package com.foodiefinder.datapipeline.writer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"dosi", "sgg"}))
public class Sgg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String dosi;
    @Column(nullable = false)
    String sgg;
    @Column(nullable = false)
    Double lon;
    @Column(nullable = false)
    Double lat;

    @Builder
    public Sgg(String dosi, String sgg, Double lon, Double lat) {
        this.dosi = dosi;
        this.sgg = sgg;
        this.lon = lon;
        this.lat = lat;
    }

    public void update(Double lon, Double lat) {
        this.lon = lon;
        this.lat = lat;
    }
}
