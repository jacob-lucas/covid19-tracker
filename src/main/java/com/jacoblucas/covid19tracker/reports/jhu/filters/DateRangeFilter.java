package com.jacoblucas.covid19tracker.reports.jhu.filters;

import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.jhu.ImmutableLocation;
import com.jacoblucas.covid19tracker.models.jhu.Location;

import javax.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class DateRangeFilter implements Filter<Location> {
    private final Date from, to;

    public DateRangeFilter(
            @Nullable final Date from,
            @Nullable final Date to
    ) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Location apply(final Location location) throws IllegalArgumentException {
        final DateFormat dateFormat = new SimpleDateFormat(JohnsHopkinsCovid19Adapter.DATE_FORMAT);

        if (from == null && to == null) {
            // nothing to filter
            return location;
        } else if (from == null) {
            throw new IllegalArgumentException("DateRangeFilter cannot be applied: must specify fromDate");
        } else if (to == null) {
            throw new IllegalArgumentException("DateRangeFilter cannot be applied: must specify toDate");
        } else if (to.before(from)) {
            throw new IllegalArgumentException(String.format("DateRangeFilter cannot be applied: fromDate [%s] is not before toDate [%s]",
                    dateFormat.format(from), dateFormat.format(to)));
        }

        final Map<String, Integer> filtered = location.getDateCountData().entrySet()
                .stream()
                .filter(e -> e.getKey().equals(from) || e.getKey().after(from))
                .filter(e -> e.getKey().equals(to) || e.getKey().before(to))
                .collect(Collectors.toMap(
                        e -> dateFormat.format(e.getKey()),
                        Map.Entry::getValue));

        return ImmutableLocation.copyOf(location)
                .withRawCountData(filtered);
    }
}
