package com.jacoblucas.covid19tracker.reports.jhu.filters;

import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateRangeFilterTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(JohnsHopkinsCovid19Adapter.DATE_FORMAT);

    // contains US location data between 1/22/20 and 3/25/20
    private Location location;

    @Before
    public void setUp() throws IOException {
        final List<String> rawTsd = InputReader.read("time_series_covid19_confirmed_global.csv")
                .collect(Collectors.toList());
        final List<Location> locations = Location.parse(rawTsd, LocationDataType.CONFIRMED_CASES);
        location = Location.aggregateByCountry(
                locations.stream()
                        .filter(loc -> loc.getCountry().equals("US"))
                        .collect(Collectors.toList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDateRangeFilterNullFrom() throws ParseException {
        final Date to = DATE_FORMAT.parse("2/17/20");

        final DateRangeFilter filter = new DateRangeFilter(null, to);
        filter.apply(location);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDateRangeFilterNullTo() throws ParseException {
        final Date from = DATE_FORMAT.parse("3/17/20");

        final DateRangeFilter filter = new DateRangeFilter(from, null);
        filter.apply(location);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDateRangeFilterToBeforeFrom() throws ParseException {
        final Date from = DATE_FORMAT.parse("3/17/20");
        final Date to = DATE_FORMAT.parse("2/17/20");

        final DateRangeFilter filter = new DateRangeFilter(from, to);
        filter.apply(location);
    }

    @Test
    public void testDateRangeFilterBothNull() {
        final DateRangeFilter filter = new DateRangeFilter(null, null);
        final Location filteredLocation = filter.apply(location);
        assertThat(filteredLocation, is(location));
    }

    @Test
    public void testDateRangeFilterFromBeforeTo() throws ParseException {
        final Date from = DATE_FORMAT.parse("2/17/20");
        final Date to = DATE_FORMAT.parse("3/17/20");

        final DateRangeFilter filter = new DateRangeFilter(from, to);
        final Location filteredLocation = filter.apply(location);

        final Map<Date, Integer> data = filteredLocation.getDateCountData();
        assertThat(data.keySet().stream().min(Comparator.naturalOrder()).get(), is(from));
        assertThat(data.keySet().stream().max(Comparator.naturalOrder()).get(), is(to));
        assertThat(data.size(), is(30));
    }

    @Test
    public void testDateRangeFilterFromEqualsTo() throws ParseException {
        final Date from = DATE_FORMAT.parse("2/17/20");

        final DateRangeFilter filter = new DateRangeFilter(from, from);
        final Location filteredLocation = filter.apply(location);


        final Map<Date, Integer> data = filteredLocation.getDateCountData();
        assertThat(data.keySet().stream().min(Comparator.naturalOrder()).get(), is(from));
        assertThat(data.keySet().stream().max(Comparator.naturalOrder()).get(), is(from));
        assertThat(data.size(), is(1));
    }

    @Test
    public void testDateRangeFilterFromBeforeMinHistory() throws ParseException {
        final Date from = DATE_FORMAT.parse("2/17/19");
        final Date to = DATE_FORMAT.parse("1/26/20");

        final DateRangeFilter filter = new DateRangeFilter(from, to);
        final Location filteredLocation = filter.apply(location);

        final Map<Date, Integer> data = filteredLocation.getDateCountData();
        assertThat(data.keySet().stream().min(Comparator.naturalOrder()).get(), is(DATE_FORMAT.parse("1/22/20")));
        assertThat(data.keySet().stream().max(Comparator.naturalOrder()).get(), is(to));
        assertThat(data.size(), is(5));
    }

    @Test
    public void testDateRangeFilterToAfterMaxHistory() throws ParseException {
        final Date from = DATE_FORMAT.parse("3/21/20");
        final Date to = DATE_FORMAT.parse("1/26/21");

        final DateRangeFilter filter = new DateRangeFilter(from, to);
        final Location filteredLocation = filter.apply(location);

        final Map<Date, Integer> data = filteredLocation.getDateCountData();
        assertThat(data.keySet().stream().min(Comparator.naturalOrder()).get(), is(from));
        assertThat(data.keySet().stream().max(Comparator.naturalOrder()).get(), is(DATE_FORMAT.parse("3/25/20")));
        assertThat(data.size(), is(5));
    }
}
