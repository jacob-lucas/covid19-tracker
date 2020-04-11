package com.jacoblucas.covid19tracker.models;

import com.jacoblucas.covid19tracker.models.jhu.LocationSummary;
import org.immutables.value.Value;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Value.Immutable
public abstract class WorldDataSummaryReport {
    public abstract String getReportGeneratedAt();

    @Value.Derived
    public String getUpdatedDate() {
        final Date mostRecentUpdateAt = getLocationSummaries().stream()
                .map(LocationSummary::getUpdatedAt)
                .max(Comparator.naturalOrder())
                .orElse(new Date());
        return new SimpleDateFormat("YYYY-MM-dd").format(mostRecentUpdateAt);
    }

    public abstract List<LocationSummary> getLocationSummaries();
}
