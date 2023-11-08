package com.foodiefinder.datapipeline.writer.entity;

import com.foodiefinder.common.entity.BaseTimeEntity;
import com.foodiefinder.restaurants.entity.Rating;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 실제 API에서 이용되는 전처리 된 데이터입니다.
 *
 * @author hyerijang
 * @version 1.0
 * @see com.foodiefinder.common.entity.BaseTimeEntity
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"businessPlaceName", "roadAddress"}))
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    /**
     * 시군명 : SIGUN_NM
     */
    @Column(nullable = false)
    private String sigunName;


    /**
     * 사업장명 : BIZPLC_NM
     */
    @Column(nullable = false)
    private String businessPlaceName;

    /**
     * 영업상태명 : BSN_STATE_NM
     */
    @Column(nullable = false)
    private String businessStateName;

    /**
     * 위생업태명 : SANITTN_BIZCOND_NM ,
     */
    @Column(nullable = false)
    private String sanitationBusinessCondition;

    /**
     * 소재지도로명주소 : REFINE_ROADNM_ADDR
     */
    @Column(nullable = false)
    private String roadAddress;

    /**
     * 소재지지번주소 : REFINE_LOTNO_ADDR
     */
    @Column(nullable = false)
    private String lotNumberAddress;

    /**
     * 소재지우편번호 : REFINE_ZIP_CD
     */
    @Column(nullable = false)
    private Integer zipCode;

    /**
     * WGS84위도 : REFINE_WGS84_LAT
     */
    @Column(nullable = false)
    private Double latitude;

    /**
     * WGS84경도 : REFINE_WGS84_LOGT
     */
    @Column(nullable = false)
    private Double longitude;

    /**
     * 해당 맛집의 평균 평점, 평점이 하나도 없는 경우 null
     */
    private Integer averageRating;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    // == 빌더 == //

    @Builder
    public Restaurant(String sigunName, String businessPlaceName, String businessStateName, String sanitationBusinessCondition, String roadAddress, String lotNumberAddress, Integer zipCode, Double latitude, Double longitude, Integer averageRating) {
        this.sigunName = sigunName;
        this.businessPlaceName = businessPlaceName;
        this.businessStateName = businessStateName;
        this.sanitationBusinessCondition = sanitationBusinessCondition;
        this.roadAddress = roadAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageRating = averageRating;

    }

    public void addRating(int average) {
        this.averageRating = average;
    }
}