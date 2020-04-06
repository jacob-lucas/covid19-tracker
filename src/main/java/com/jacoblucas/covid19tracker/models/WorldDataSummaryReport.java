package com.jacoblucas.covid19tracker.models;

import com.jacoblucas.covid19tracker.models.jhu.LocationSummary;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Value.Immutable
public abstract class WorldDataSummaryReport {
    public abstract String getReportGeneratedAt();

    public abstract List<LocationSummary> getLocationSummaries();

    @Value.Derived
    public String getUpdatedDate() {
        final Date mostRecentUpdateAt = getLocationSummaries().stream()
                .map(LocationSummary::getUpdatedAt)
                .max(Comparator.naturalOrder())
                .get();
        return mostRecentUpdateAt.toInstant().toString();
    }
}
