package com.jacoblucas.covid19tracker.reports;

import com.jacoblucas.covid19tracker.models.Pair;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Value.Immutable
public abstract class DailyConfirmedCasesDeltaReport {
    public abstract Instant getReportGeneratedAt();

    public abstract Map<String, String> getFilterMap();

    public abstract List<Pair<Instant, Integer>> getConfirmedCasesDeltas();
}