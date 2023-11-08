package com.foodiefinder.datapipeline.util.request;

import java.util.Map;

public interface UrlProvider<T> {
    T getUrl(String url, Map<String, String> value);
}
