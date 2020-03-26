package com.jacoblucas.covid19tracker.adapters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.Location;
import com.jacoblucas.covid19tracker.models.Locations;
import com.jacoblucas.covid19tracker.utils.ObjectMapperFactory;

import java.io.IOException;
import java.util.Map;

public class Covid19Adapter {
    public static final String BASE_URL = "https://coronavirus-tracker-api.herokuapp.com/v2/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Covid19Adapter() {
        this(new HttpClient());
    }

    public Covid19Adapter(final HttpClient httpClient) {
        this.httpClient = httpClient;
        objectMapper = ObjectMapperFactory.getInstance();
    }

    public Locations getAllLocations(final boolean includeTimelines) throws IOException {
        final Map<String, String> querystringMap = ImmutableMap.of("timelines", includeTimelines ? "1" : "0");
        return getLocations(querystringMap);
    }

    public Locations getLocationsByCountry(final String countryCode, final boolean includeTimelines) throws IOException {
        final Map<String, String> querystringMap = ImmutableMap.of(
                "timelines", includeTimelines ? "1" : "0",
                "country_code", countryCode);
        return getLocations(querystringMap);
    }

    public Locations getLocations(final Map<String, String> filters) throws IOException {
        final String json = httpClient.get(BASE_URL + "locations", filters);
        return objectMapper.readValue(json, new TypeReference<Locations>() {});
    }

    public Location getLocation(final int id) throws IOException {
        final String json = httpClient.get(BASE_URL + "locations/" + id);
        final Map<String, Location> response = objectMapper.readValue(json, new TypeReference<Map<String, Location>>() {});
        return response.get("location");
    }
}
