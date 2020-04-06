package com.jacoblucas.covid19tracker.models;

import com.jacoblucas.covid19tracker.models.jhu.LocationSummary;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Value.Immutable
public abstract class WorldDataSummaryReport {
    public abstract String getReportGeneratedAt();

    @Value.Derived
    public Date getUpdatedDate() {
        return getLocationSummaries().stream()
                .map(LocationSummary::getUpdatedAt)
                .max(Comparator.naturalOrder())
                .get();
    }

    public abstract List<LocationSummary> getLocationSummaries();
}
