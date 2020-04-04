package com.jacoblucas.covid19tracker.iot.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ImmutableDailyNewCasesReportRequest.class)
public abstract class DailyNewCasesReportRequest {
    public abstract Map<String, String> getFilters();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Optional<String> getFromDate() {
        return Optional.ofNullable(getFilters().get("fromDate"));
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Optional<String> getToDate() {
        return Optional.ofNullable(getFilters().get("toDate"));
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
