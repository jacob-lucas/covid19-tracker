package com.jacoblucas.covid19tracker.reports;

import com.jacoblucas.covid19tracker.adapters.Covid19Adapter;
import com.jacoblucas.covid19tracker.models.ImmutablePair;
import com.jacoblucas.covid19tracker.models.Location;
import com.jacoblucas.covid19tracker.models.Locations;
import com.jacoblucas.covid19tracker.models.Metrics;
import com.jacoblucas.covid19tracker.models.Pair;
import com.jacoblucas.covid19tracker.models.Timeline;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportGenerator {
    private final Covid19Adapter covid19Adapter;

    public ReportGenerator(final Covid19Adapter covid19Adapter) {
        this.covid19Adapter = covid19Adapter;
    }

    // TODO: date filtering
    public DailyConfirmedCasesDeltaReport generateDailyConfirmedCasesDeltaReport(
            final Map<String, String> filters
    ) throws IOException {
        Map<String, String> customFilter = new HashMap<>(filters);
        if (!customFilter.containsKey("timelines")) {
            customFilter.put("timelines", "1");
        }

        if (!customFilter.containsKey("source")) {
            customFilter.put("source", "jhu");
        }

        final String source = customFilter.get("source");
        final Locations locations = covid19Adapter.getLocations(customFilter);
        final List<Pair<Instant, Integer>> data = getConfirmedCasesDeltas(locations);
        return ImmutableDailyConfirmedCasesDeltaReport.builder()
                .reportGeneratedAt(Instant.now())
                .source(source)
                .filters(filters)
                .currentTotalConfirmed(locations.getLatest().get(Metrics.CONFIRMED))
                .confirmedCasesDeltas(data)
                .build();
    }

    private List<Pair<Instant, Integer>> getConfirmedCasesDeltas(final Locations locations) {
        final Map<Instant, Integer> collected = locations.getLocations().stream()
                .map(Location::getTimelines)
                .map(m -> m.get(Metrics.CONFIRMED))
                .filter(Objects::nonNull)
                .map(Timeline::getTimeline)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

        final List<Instant> days = collected.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        final List<Pair<Instant, Integer>> results = new ArrayList<>();
        for (int i=1; i<days.size(); i++) {
            final Instant today = days.get(i);
            final Instant yesterday = days.get(i-1);

            final int casesToday = collected.get(today);
            final int casesYesterday = collected.get(yesterday);

            results.add(ImmutablePair.of(today, casesToday - casesYesterday));
        }

        return results;
    }
}
