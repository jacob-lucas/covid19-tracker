package com.jacoblucas.covid19tracker.reports.jhu.filters;

import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CountryFilterTest {
    // contains US location data between 1/22/20 and 3/25/20
    private Location location;

    @Before
    public void setUp() throws IOException {
        final List<String> rawTsd = InputReader.read("time_series_covid19_confirmed_global.csv")
                .collect(Collectors.toList());
        final List<Location> locations = Location.parse(rawTsd);
        location = Location.aggregateByCountry(
                locations.stream()
                        .filter(loc -> loc.getCountry().equals("US"))
                        .collect(Collectors.toList()));
    }

    @Test
    public void testFilterForNull() {
        final CountryFilter filter = new CountryFilter(null);
        assertThat(filter.test(location), is(false));
    }

    @Test
    public void testFilterForKnownCountry() {
        final CountryFilter filter = new CountryFilter("US");
        assertThat(filter.test(location), is(true));
    }

    @Test
    public void testFilterForUnknownCountry() {
        final CountryFilter filter = new CountryFilter("Italy");
        assertThat(filter.test(location), is(false));
    }
}
