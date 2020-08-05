package com.jacoblucas.covid19tracker.adapters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.ctp.StateMetadata;
import com.jacoblucas.covid19tracker.models.ctp.StateSummary;
import com.jacoblucas.covid19tracker.models.ctp.UnitedStatesSummary;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// See https://covidtracking.com/data/api for more information
public class CovidTrackingProjectAdapter {
    private static final String US_CURRENT_URL = "/api/v1/us/current.json";
    private static final String US_HISTORIC_URL = "/api/v1/us/daily.json";
    private static final String STATE_METADATA_URL = "/api/v1/states/info.json";
    private static final String SPECIFIC_STATE_METADATA_URL = "/api/v1/states/%s/info.json";
    private static final String ALL_STATES_CURRENT_URL = "/api/v1/states/current.json";
    private static final String ALL_STATES_HISTORIC_URL = "/api/v1/states/daily.json";
    private static final String SPECIFIC_STATE_CURRENT_URL = "/api/v1/states/%s/current.json";
    private static final String SPECIFIC_STATE_HISTORIC_URL = "/api/v1/states/%s/daily.json";

    private final HttpClient httpClient;
    private final String dataLocation;
    private final ObjectMapper objectMapper;

    public CovidTrackingProjectAdapter(
            final HttpClient httpClient,
            final String dataLocation,
            final ObjectMapper objectMapper
    ) {
        this.httpClient = httpClient;
        this.dataLocation = dataLocation;
        this.objectMapper = objectMapper;
    }

    public UnitedStatesSummary getCurrentUnitedStatesSummary() throws IOException {
        final String response = httpClient.get(dataLocation + US_CURRENT_URL);

        final List<UnitedStatesSummary> summary = objectMapper.readValue(response, new TypeReference<List<UnitedStatesSummary>>() {});
        return summary.get(0);
    }

    public List<UnitedStatesSummary> getHistoricUnitedStatesSummary() throws IOException {
        return getHistoricUnitedStatesSummary(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public List<UnitedStatesSummary> getHistoricUnitedStatesSummary(final int from, final int to) throws IOException {
        final String response = httpClient.get(dataLocation + US_HISTORIC_URL);

        final List<UnitedStatesSummary> summary = objectMapper.readValue(response, new TypeReference<List<UnitedStatesSummary>>() {});

        return summary.stream()
                .filter(s -> s.getDate() >= from && s.getDate() <= to)
                .sorted(Comparator.comparing(UnitedStatesSummary::getDate))
                .collect(Collectors.toList());
    }

    public Map<String, StateMetadata> getStateMetadata() throws IOException {
        final String response = httpClient.get(dataLocation + STATE_METADATA_URL);

        final List<StateMetadata> stateMetadata = objectMapper.readValue(response, new TypeReference<List<StateMetadata>>() {});

        return Maps.uniqueIndex(stateMetadata, StateMetadata::getState);
    }

    public StateMetadata getSpecificStateMetadata(final String state) throws IOException {
        final String response = httpClient.get(String.format(dataLocation + SPECIFIC_STATE_METADATA_URL, state.toLowerCase()));

        return objectMapper.readValue(response, new TypeReference<StateMetadata>() {});
    }

    public Map<String, StateSummary> getCurrentValuesForAllStates() throws IOException {
        final String response = httpClient.get(dataLocation + ALL_STATES_CURRENT_URL);

        final List<StateSummary> stateResultsSummaries = objectMapper.readValue(response, new TypeReference<List<StateSummary>>() {});

        return Maps.uniqueIndex(stateResultsSummaries, StateSummary::getState);
    }

    public StateSummary getCurrentValuesForState(final String state) throws IOException {
        final String response = httpClient.get(String.format(dataLocation + SPECIFIC_STATE_CURRENT_URL, state.toLowerCase()));

        return objectMapper.readValue(response, new TypeReference<StateSummary>() {});
    }

    public <T> Map<T, List<StateSummary>> getHistoricValuesForAllStates(final Function<StateSummary, T> keyExtractor) throws IOException {
        final String response = httpClient.get(dataLocation + ALL_STATES_HISTORIC_URL);

        final List<StateSummary> stateResultsSummaries = objectMapper.readValue(response, new TypeReference<List<StateSummary>>() {});

        return stateResultsSummaries.stream()
                .sorted(Comparator.comparing(StateSummary::getDate))
                .sorted(Comparator.comparing(StateSummary::getState))
                .collect(Collectors.groupingBy(keyExtractor));
    }

    public List<StateSummary> getHistoricValuesForState(final String state) throws IOException {
        return getHistoricValuesForState(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public List<StateSummary> getHistoricValuesForState(final String state, final int from, final int to) throws IOException {
        final String response = httpClient.get(String.format(dataLocation + SPECIFIC_STATE_HISTORIC_URL, state.toLowerCase()));

        final List<StateSummary> stateResultsSummaries = objectMapper.readValue(response, new TypeReference<List<StateSummary>>() {});

        return stateResultsSummaries.stream()
                .filter(s -> s.getDate() >= from && s.getDate() <= to)
                .sorted(Comparator.comparing(StateSummary::getDate))
                .collect(Collectors.toList());
    }

}
