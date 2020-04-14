package com.jacoblucas.covid19tracker.reports.jhu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.ImmutableDailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.ImmutableTrend;
import com.jacoblucas.covid19tracker.models.ImmutableWorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.Trend;
import com.jacoblucas.covid19tracker.models.WorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.jhu.ImmutableLocation;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.models.jhu.LocationSummary;
import com.jacoblucas.covid19tracker.reports.jhu.filters.CountryFilter;
import com.jacoblucas.covid19tracker.reports.jhu.filters.DateRangeFilter;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    static final DateFormat DATE_FORMAT = new SimpleDateFormat(JohnsHopkinsCovid19Adapter.DATE_FORMAT);

    private final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter;

    public ReportGenerator(final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter) {
        this.johnsHopkinsCovid19Adapter = johnsHopkinsCovid19Adapter;
    }

    public WorldDataSummaryReport generateWorldDataSummary(final LocationDataType locationDataType) throws IOException {
        final List<Location> allLocationData = johnsHopkinsCovid19Adapter.getAllLocationData(locationDataType);
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

    public DailyNewCasesReport generateDailyNewCasesReport(
            final Map<String, String> filters,
            final LocationDataType locationDataType
    ) throws IOException {
        final List<Location> allLocationData = johnsHopkinsCovid19Adapter.getAllLocationData(locationDataType);
        final List<Location> filteredLocations = filter(allLocationData, filters);

        final List<Location> dailyNewCases = getConfirmedCaseDeltas(filteredLocations);

        final int total = dailyNewCases.stream()
                .map(Location::getTotal)
                .reduce(0, Integer::sum);

        Date trendThreshold = DateUtils.addDays(new Date(), -1);
        if (filters.containsKey("toDate")) {
            try {
                trendThreshold = DATE_FORMAT.parse(filters.get("toDate"));
            } catch (ParseException e) {
                // do nothing
            }
        }

        final Map<String, Trend> trendMap;
        if (locationDataType == LocationDataType.CONFIRMED_CASES) {
            final Map<String, Trend> allTrends = calculateTrendsToDate(trendThreshold, filteredLocations);
            if (filters.containsKey("country")) {
                trendMap = allTrends.entrySet().stream()
                        .filter(e -> e.getValue().getLocation().equals(filters.get("country")))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            } else {
                trendMap = allTrends;
            }
        } else {
            trendMap = ImmutableMap.of();
        }

        return ImmutableDailyNewCasesReport.builder()
                .reportGeneratedAt(Instant.now().toString())
                .source(JohnsHopkinsCovid19Adapter.NAME)
                .filters(filters)
                .total(total)
                .dailyNewCases(dailyNewCases)
                .countryTrends(trendMap)
                .build();
    }

    private Map<String, Trend> calculateTrendsToDate(final Date endDate, final List<Location> allLocationData) {
        final Map<Integer, List<Location>> windowData = new HashMap<>();
        final int daysPerWindow = 1;
        final int windows = 7;
        for (int i = windows-1; i >= 0; i--) {
            final Date to = DateUtils.addDays(endDate, -1 * i * daysPerWindow);
            final Date from = DateUtils.addDays(to, -1 * daysPerWindow);

            final Map<String, String> filters = ImmutableMap.of(
                    "fromDate", DATE_FORMAT.format(from),
                    "toDate", DATE_FORMAT.format(to));

            final List<Location> filteredLocations = filter(allLocationData, filters);

            final List<Location> confirmedCaseDeltas = getConfirmedCaseDeltas(filteredLocations)
                    .stream()
                    .map(loc -> {
                        try {
                            final Map<String, Integer> rawCountData = loc.getRawCountData();
                            final Map<String, Integer> withinRangeData = new HashMap<>();
                            for (Map.Entry<String, Integer> entry : rawCountData.entrySet()) {
                                final Date date = DATE_FORMAT.parse(entry.getKey());
                                if (date.compareTo(from) >= 0 && date.compareTo(to) < 0) {
                                    withinRangeData.put(entry.getKey(), entry.getValue());
                                }
                            }
                            return ImmutableLocation.copyOf(loc)
                                    .withRawCountData(withinRangeData);
                        } catch (final Exception e) {
                            System.out.println(e.getMessage());
                            return loc;
                        }
                    })
                    .collect(Collectors.toList());

            windowData.put(i, confirmedCaseDeltas);
        }

        return Maps.uniqueIndex(windowData.values().stream()
                .flatMap(List::stream)
                .map(Location::getCountry)
                .distinct()
                .map(country -> {
                    final List<Integer> counts = new ArrayList<>();
                    for (int i=0; i<windows; i++) {
                        final Integer count = windowData.get(i).stream()
                                .filter(ls -> ls.getCountry().equals(country))
                                .findFirst()
                                .map(Location::getTotal)
                                .orElse(0);
                        counts.add(count);
                    }
                    return ImmutableTrend.builder()
                            .location(country)
                            .windows(counts)
                            .windowSize(daysPerWindow)
                            .build();
                })
                .collect(Collectors.toList()), Trend::getLocation);
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
