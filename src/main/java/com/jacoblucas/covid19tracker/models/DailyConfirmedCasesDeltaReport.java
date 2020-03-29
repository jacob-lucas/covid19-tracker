package com.jacoblucas.covid19tracker.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableDailyConfirmedCasesDeltaReport.class)
public abstract class DailyConfirmedCasesDeltaReport {
    // Timestamp in ISO 8601 format
    public abstract String getReportGeneratedAt();

    public abstract String getSource();

    public abstract Map<String, String> getFilters();

    public abstract int getCurrentTotalConfirmed();

    public abstract List<Location> getConfirmedCasesDeltas();
}