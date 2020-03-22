package com.jacoblucas.covid19tracker.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient {
    private final ObjectMapper objectMapper;

    public HttpClient() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new GuavaModule());
    }

    public <T> T get(
            final String url,
            final TypeReference<T> responseType
    ) throws IOException {
        return get(url, ImmutableMap.of(), responseType);
    }

    public <T> T get(
            final String url,
            final Map<String, String> querystringMap,
            final TypeReference<T> responseType
    ) throws IOException {
        final String querystring = toQuerystring(querystringMap);
        final String json = Request.Get(url + querystring)
                .connectTimeout(1000)
                .socketTimeout(1000)
                .execute()
                .returnContent()
                .asString();

        return objectMapper.readValue(json, responseType);
    }

    String toQuerystring(final Map<String, String> querystringMap) {
        return "?" + querystringMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
