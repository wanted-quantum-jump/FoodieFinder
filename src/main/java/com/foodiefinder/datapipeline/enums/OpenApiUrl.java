package com.foodiefinder.datapipeline.enums;

// 사용할 url 이 있다면 추가
public enum OpenApiUrl {
    LAUNCH("Genrestrtlunch"), // 김밥 도시락
    CAFE("Genrestrtcate"), // 카페
    MNTCOOK("Genrestrtmovmntcook"), // 일반 음식점(이동 조리)
    CHIFOOD("Genrestrtchifood"), // 중식
    JPNFOOD("Genrestrtjpnfood"), // 일식
    SOUP("Genrestrtsoup"), // 탕류
    FASTFOOD("Genrestrtfastfood"), // 패스트푸드
    SASH("Genrestrtsash"), // 생선회
    BUFF("Genrestrtbuff"), // 뷔페식
    FODTUCK("Resrestrtfodtuck"), // 푸드 트럭
    FUGU("Genrestrtfugu"),
    STANDPUB("Genrestrtstandpub"),
    TRATTERM("Genrestrttratearm"),
    BSRPCOOK("Genrestrtbsrpcook")
        ;
    private final String url;

    OpenApiUrl(String url){
        this.url = url;
    }

    public String getUrl() {
        return "https://openapi.gg.go.kr/" + url;
    }
}
