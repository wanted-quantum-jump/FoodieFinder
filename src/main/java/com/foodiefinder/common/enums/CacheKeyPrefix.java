package com.foodiefinder.common.enums;

public enum CacheKeyPrefix {

    DATAPIPELINE_RESPONSE("datapipeline:response:"),
    MAP_KOREA("map:korea"),
    MAP_SGG("map:"), // ~ 시군구 단위의 이름
    RESTAURANT_KEY_PREFIX("restaurant:")
    ;

    CacheKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private String keyPrefix;

    public String getKeyPrefix() {
        return keyPrefix;
    }
}
