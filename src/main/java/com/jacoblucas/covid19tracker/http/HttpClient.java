package com.jacoblucas.covid19tracker.http;

import com.google.common.collect.ImmutableMap;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient {

    public String get(final String url) throws IOException {
        return get(url, ImmutableMap.of());
    }

    public String get(
            final String url,
            final Map<String, String> querystringMap
    ) throws IOException {
        final String querystring = toQuerystring(querystringMap);
        return Request.Get(url + querystring)
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnContent()
                .asString();
    }

    String toQuerystring(final Map<String, String> querystringMap) {
        return "?" + querystringMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
