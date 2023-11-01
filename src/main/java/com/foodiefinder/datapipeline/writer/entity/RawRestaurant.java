package com.foodiefinder.datapipeline.writer.entity;

import com.foodiefinder.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * raw 데이터 저장을 위한 엔티티입니다. 공공 API 결과값을 변환하거나 누락하지 않고 그대로 저장합니다.
 *
 * @author hyerijang
 * @version 1.0
 * @see com.foodiefinder.common.entity.BaseTimeEntity
 */

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"businessPlaceName", "roadAddress"}))
public class RawRestaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    /**
     * 행총건수 : LIST_TOTAL_COUNT
     */
    private Integer listTotalCount;

    /**
     * 응답결과코드 : CODE
     */
    private String code;

    /**
     * 응답결과메세지 : MESSAGE
     */
    private String message;

    /**
     * API버전 : API_VERSION
     */
    private String apiVersion;

    /**
     * 시군명 : SIGUN_NM
     */
    private String sigunName;

    /**
     * 시군코드 : SIGUN_CD
     */
    private String sigunCode;

    /**
     * 사업장명 : BIZPLC_NM
     */
    private String businessPlaceName;

    /**
     * 인허가일자 : LICENSG_DE
     */
    private String licenseDate;

    /**
     * 영업상태명 : BSN_STATE_NM
     */
    private String businessStateName;

    /**
     * 폐업일자 : CLSBIZ_DE
     */
    private String closeDate;

    /**
     * 소재지면적(㎡) : LOCPLC_AR
     */
    private Double locationArea;

    /**
     * 급수시설구분명 : GRAD_FACLT_DIV_NM
     */
    private String waterFacilityTypeName;

    /**
     * 남성종사자수(명) : MALE_ENFLPSN_CNT
     */
    private Integer maleEmployeeCount;

    /**
     * 년도 : YY
     */
    private Integer year;

    /**
     * 다중이용업소여부 : MULTI_USE_BIZESTBL_YN
     */
    private String multiUseBusinessEstablishment;

    /**
     * 등급구분명 : GRAD_DIV_NM
     */
    private String gradeDivisionName;

    /**
     * 총시설규모(㎡) : TOT_FACLT_SCALE
     */
    private Double totalFacilityScale;

    /**
     * 여성종사자수(명) : FEMALE_ENFLPSN_CNT
     */
    private Integer femaleEmployeeCount;

    /**
     * 영업장주변구분명 : BSNSITE_CIRCUMFR_DIV_NM
     */
    private String businessSiteCircumferenceTypeName;

    /**
     * 위생업종명 : SANITTN_INDUTYPE_NM
     */
    private String sanitationIndustryType;

    /**
     * 위생업태명 : SANITTN_BIZCOND_NM
     */
    private String sanitationBusinessCondition;

    /**
     * 총종업원수 : TOT_EMPLY_CNT
     */
    private Integer totalEmployeeCount;

    /**
     * 소재지도로명주소 : REFINE_ROADNM_ADDR
     */

    private String roadAddress;

    /**
     * 소재지지번주소 : REFINE_LOTNO_ADDR
     */
    private String lotNumberAddress;

    /**
     * 소재지우편번호 : REFINE_ZIP_CD
     */
    private Integer zipCode;

    /**
     * WGS84위도 : REFINE_WGS84_LAT
     */
    private Double latitude;

    /**
     * WGS84경도 : REFINE_WGS84_LOGT
     */
    private Double longitude;

    // == 빌더 == //
    @Builder
    public RawRestaurant(Integer listTotalCount, String code, String message, String apiVersion, String sigunName, String sigunCode, String businessPlaceName, String licenseDate, String businessStateName, String closeDate, Double locationArea, String waterFacilityTypeName, Integer maleEmployeeCount, Integer year, String multiUseBusinessEstablishment, String gradeDivisionName, Double totalFacilityScale, Integer femaleEmployeeCount, String businessSiteCircumferenceTypeName, String sanitationIndustryType, String sanitationBusinessCondition, Integer totalEmployeeCount, String roadAddress, String lotNumberAddress, Integer zipCode, Double latitude, Double longitude) {
        this.listTotalCount = listTotalCount;
        this.code = code;
        this.message = message;
        this.apiVersion = apiVersion;
        this.sigunName = sigunName;
        this.sigunCode = sigunCode;
        this.businessPlaceName = businessPlaceName;
        this.licenseDate = licenseDate;
        this.businessStateName = businessStateName;
        this.closeDate = closeDate;
        this.locationArea = locationArea;
        this.waterFacilityTypeName = waterFacilityTypeName;
        this.maleEmployeeCount = maleEmployeeCount;
        this.year = year;
        this.multiUseBusinessEstablishment = multiUseBusinessEstablishment;
        this.gradeDivisionName = gradeDivisionName;
        this.totalFacilityScale = totalFacilityScale;
        this.femaleEmployeeCount = femaleEmployeeCount;
        this.businessSiteCircumferenceTypeName = businessSiteCircumferenceTypeName;
        this.sanitationIndustryType = sanitationIndustryType;
        this.sanitationBusinessCondition = sanitationBusinessCondition;
        this.totalEmployeeCount = totalEmployeeCount;
        this.roadAddress = roadAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}