package com.jacoblucas.covid19tracker.reports.jhu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.ImmutableDailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.ImmutableWorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.WorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.jhu.ImmutableLocation;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.models.jhu.LocationSummary;
import com.jacoblucas.covid19tracker.reports.jhu.filters.CountryFilter;
import com.jacoblucas.covid19tracker.reports.jhu.filters.DateRangeFilter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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

    public WorldDataSummaryReport generateWorldDataSummary() throws IOException {
        final List<Location> allLocationData = johnsHopkinsCovid19Adapter.getAllLocationData();
        final Map<String, List<Location>> locationsByCountry = allLocationData.stream()
                .collect(Collectors.groupingBy(Location::getCountry));

        final List<LocationSummary> summaries = locationsByCountry.values()
                .stream()
                .map(Location::aggregateByCountry)
                .map(LocationSummary::generate)
                .sorted(Comparator.comparing(LocationSummary::getCountry))
                .collect(Collectors.toList());

        return ImmutableWorldDataSummaryReport.builder()
                .reportGeneratedAt(Instant.now().toString())
                .locationSummaries(summaries)
                .build();
    }

    public DailyNewCasesReport generateDailyNewCasesReport(final Map<String, String> filters) throws IOException {
        final List<Location> allLocationData = johnsHopkinsCovid19Adapter.getAllLocationData();
        final List<Location> filteredLocations = filter(allLocationData, filters);

        final List<Location> dailyNewCases = getConfirmedCaseDeltas(filteredLocations);

        final int total = dailyNewCases.stream()
                .map(loc -> loc.getDateCountData().values()
                        .stream()
                        .reduce(0, Integer::sum))
                .reduce(0, Integer::sum);

        return ImmutableDailyNewCasesReport.builder()
                .reportGeneratedAt(Instant.now().toString())
                .source(JohnsHopkinsCovid19Adapter.NAME)
                .filters(filters)
                .total(total)
                .dailyNewCases(dailyNewCases)
                .build();
    }

    final List<Location> filter(final List<Location> locationData, final Map<String, String> filters) {
        final String country = filters.get("country");
        final String state = filters.get("state");
        final String fromDateStr = filters.get("fromDate");
        final String toDateStr = filters.get("toDate");

        Date from;
        Date to;
        try {
            from = new Date(DATE_FORMAT.parse(fromDateStr).toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli());
            to = DATE_FORMAT.parse(toDateStr);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(String.format("Invalid dates provided for filtering: fromDate [%s] toDate [%s]", fromDateStr, toDateStr), e);
        } catch (final NullPointerException e) {
            from = null;
            to = null;
        }

        final DateRangeFilter dateRangeFilter = new DateRangeFilter(from, to);
        final List<Location> filtered = locationData.stream()
                .map(dateRangeFilter::apply)
                .filter(new CountryFilter(country))
                .collect(Collectors.toList());

        if (state == null && country != null && filtered.size() > 1) {
            // aggregate by country if multiple locations (states) are reporting data
            return ImmutableList.of(Location.aggregateByCountry(filtered));
        } else {
            return filtered;
        }
    }

    private List<Location> getConfirmedCaseDeltas(final List<Location> locations) {
        return locations.stream()
                .map(loc -> {
                    final Map<Date, Integer> dateCountData = loc.getDateCountData();
                    final List<Date> days = dateCountData.keySet().stream()
                            .sorted()
                            .collect(Collectors.toList());

                    final Map<String, Integer> deltas = Maps.newTreeMap();
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
