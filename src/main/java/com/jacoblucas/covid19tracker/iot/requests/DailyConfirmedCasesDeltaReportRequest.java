package com.jacoblucas.covid19tracker.iot.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableDailyConfirmedCasesDeltaReportRequest.class)
public abstract class DailyConfirmedCasesDeltaReportRequest {
    public abstract Map<String, String> getFilters();

    @Value.Derived
    public Optional<String> getFromDate() {
        return Optional.ofNullable(getFilters().get("fromDate"));
    }

    @Value.Derived
    public Optional<String> getToDate() {
        return Optional.ofNullable(getFilters().get("toDate"));
    }

    public boolean containsDateFilter() {
        return getFromDate().isPresent() && getToDate().isPresent();
    }

    @Value.Check
    public void check() {
        final boolean fromDatePresent = getFromDate().isPresent();
        final boolean toDatePresent = getToDate().isPresent();
        if (fromDatePresent && !toDatePresent) {
            throw new IllegalArgumentException("Must specify toDate if fromDate is specified");
        }

        if (!fromDatePresent && toDatePresent) {
            throw new IllegalArgumentException("Must specify fromDate if toDate is specified");
        }
    }
}
