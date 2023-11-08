package com.foodiefinder.datapipeline.util.response;

import java.util.Map;

public interface UrlProvider<T> {
    T getUrl(String url, Map<String, String> value);
}
