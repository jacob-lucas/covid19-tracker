package com.jacoblucas.covid19tracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import org.immutables.value.Value;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value.Immutable
@JsonDeserialize(as = ImmutableDailyNewCasesReport.class)
public abstract class DailyNewCasesReport {
    // Timestamp in ISO 8601 format
    public abstract String getReportGeneratedAt();

    public abstract String getSource();

    public abstract Map<String, String> getFilters();

    public abstract int getTotal();

    public abstract List<Location> getDailyNewCases();

    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public Map<String, Trend> getCountryTrends() {
        return ImmutableMap.of();
    }

    @Value.Derived
    public Map<LocationStatus, List<Trend>> getCountryTrendsByLocationStatus() {
        return getCountryTrends().values().stream()
                .collect(Collectors.groupingBy(Trend::getLocationStatus))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(Comparator.comparing(Trend::getGradient))
                                .collect(Collectors.toList())));
    }

    @Value.Derived
    public String getUpdatedDate() {
        final Date mostRecentUpdateAt = getDailyNewCases().stream()
                .map(Location::getUpdatedAt)
                .max(Comparator.naturalOrder())
                .orElse(new Date());
        return new SimpleDateFormat("YYYY-MM-dd").format(mostRecentUpdateAt);
    }
}
