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
public class Address {

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소재지도로명주소 : REFINE_ROADNM_ADDR
     *
     */
    @Column(unique=true)
    private String roadAddress;

    /**
     * 소재지지번주소 : REFINE_LOTNO_ADDR
     */
    private String lotNumberAddress;

    /**
     * 소재지우편번호 : REFINE_ZIP_CD
     */
    private Integer zipCode;

    @JsonIgnore
    @OneToOne(mappedBy = "address", fetch = LAZY)
    private Restaurant restaurant;

    //=== 연관관계 메서드 ===//
    protected void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    //== 생성 메서드 == //
    @Builder
    public Address(String roadAddress, String lotNumberAddress, Integer zipCode) {
        this.roadAddress = roadAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.zipCode = zipCode;
    }
}
