package com.jacoblucas.covid19tracker.reports.jhu;

import com.google.common.collect.Maps;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.models.ImmutableDailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.models.jhu.ImmutableLocation;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.reports.jhu.filters.CountryFilter;
import com.jacoblucas.covid19tracker.reports.jhu.filters.DateRangeFilter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    static final DateFormat DATE_FORMAT = new SimpleDateFormat(JohnsHopkinsCovid19Adapter.DATE_FORMAT);

    private final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter;

    public ReportGenerator(final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter) {
        this.johnsHopkinsCovid19Adapter = johnsHopkinsCovid19Adapter;
    }

    public DailyConfirmedCasesDeltaReport generateDailyConfirmedCasesDeltaReport(final Map<String, String> filters) throws IOException {
        final List<Location> allLocationData = johnsHopkinsCovid19Adapter.getAllLocationData();
        final List<Location> filteredLocations = filter(allLocationData, filters);

        final int total = filteredLocations.stream()
                .map(loc -> loc.getDateCountData().values()
                        .stream()
                        .reduce(0, Integer::sum))
                .reduce(0, Integer::sum);

        final List<Location> confirmedCaseDeltas = getConfirmedCaseDeltas(filteredLocations);

        return ImmutableDailyConfirmedCasesDeltaReport.builder()
                .reportGeneratedAt(Instant.now().toString())
                .source(JohnsHopkinsCovid19Adapter.NAME)
                .filters(filters)
                .currentTotalConfirmed(total)
                .confirmedCasesDeltas(confirmedCaseDeltas)
                .build();
    }

    final List<Location> filter(final List<Location> locationData, final Map<String, String> filters) {
        final String country = filters.get("country");
        final String fromDateStr = filters.get("fromDate");
        final String toDateStr = filters.get("toDate");

       Date from;
       Date to;
        try {
            from = DATE_FORMAT.parse(fromDateStr);
            to = DATE_FORMAT.parse(toDateStr);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(String.format("Invalid dates provided for filtering: fromDate [%s] toDate [%s]", fromDateStr, toDateStr), e);
        } catch (final NullPointerException e) {
            from = null;
            to = null;
        }

        final DateRangeFilter dateRangeFilter = new DateRangeFilter(from, to);
        return locationData.stream()
                .filter(new CountryFilter(country))
                .map(dateRangeFilter::apply)
                .collect(Collectors.toList());
    }

    private List<Location> getConfirmedCaseDeltas(final List<Location> locations) {
        return locations.stream()
                .map(loc -> {
                    final Map<Date, Integer> dateCountData = loc.getDateCountData();
                    final List<Date> days = dateCountData.keySet().stream()
                            .sorted()
                            .collect(Collectors.toList());

                    final Map<String, Integer> deltas = Maps.newTreeMap();
                    deltas.put(DATE_FORMAT.format(days.get(0)), 0);
                    for (int i=1; i<days.size(); i++) {
                        final Date today = days.get(i);
                        final Date yesterday = days.get(i-1);

                        final int casesToday = dateCountData.get(today);
                        final int casesYesterday = dateCountData.get(yesterday);
                        deltas.put(DATE_FORMAT.format(today), casesToday - casesYesterday);
                    }

                    return ImmutableLocation.copyOf(loc)
                            .withRawCountData(deltas);
                })
                .collect(Collectors.toList());
    }
}
