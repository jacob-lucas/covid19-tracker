package com.jacoblucas.covid19tracker.reports.coronavirustrackerapi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jacoblucas.covid19tracker.models.Pair;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableDailyConfirmedCasesDeltaReport.class)
public abstract class DailyConfirmedCasesDeltaReport {
    public abstract Instant getReportGeneratedAt();

    public abstract String getSource();

    public abstract Map<String, String> getFilters();

    public abstract int getCurrentTotalConfirmed();

    public abstract List<Pair<Instant, Integer>> getConfirmedCasesDeltas();
}
