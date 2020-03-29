package com.jacoblucas.covid19tracker.reports.jhu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.models.jhu.ImmutableLocation;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator.DATE_FORMAT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportGeneratorTest extends TestBase {
    private static List<Location> LOCATION_DATA;

    @Mock private JohnsHopkinsCovid19Adapter mockJohnsHopkinsCovid19Adapter;

    private ReportGenerator reportGenerator;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        final List<String> rawTsd = InputReader.read("time_series_covid19_confirmed_global.csv")
                .collect(Collectors.toList());
        LOCATION_DATA = Location.parse(rawTsd);
    }

    @Before
    public void setUp() throws IOException {
        final Location usLocationData = Location.aggregateByCountry(
                LOCATION_DATA.stream()
                        .filter(loc -> loc.getCountry().equals("US"))
                        .collect(Collectors.toList()));

        when(mockJohnsHopkinsCovid19Adapter.getLocationData("US")).thenReturn(Optional.of(usLocationData));
        reportGenerator = new ReportGenerator(mockJohnsHopkinsCovid19Adapter);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockJohnsHopkinsCovid19Adapter);
    }

    @Test
    public void testFilterByValidDates() {
        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/20/20",
                "toDate", "3/25/20");
        final List<Location> filtered = reportGenerator.filter(LOCATION_DATA, filters);

        assertThat(filtered, is(not(empty())));
        assertThat(filtered.stream().allMatch(loc -> {
            final String fromStr = DATE_FORMAT.format(loc.getDateCountData().keySet().stream().min(Comparator.naturalOrder()).get());
            return fromStr.equals("03/20/20");
        }), is(true));
        assertThat(filtered.stream().allMatch(loc -> {
            final String toStr = DATE_FORMAT.format(loc.getDateCountData().keySet().stream().max(Comparator.naturalOrder()).get());
            return toStr.equals("03/25/20");
        }), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterMissingFromDate() {
        final Map<String, String> filters = ImmutableMap.of(
                "toDate", "3/25/20");
        reportGenerator.filter(LOCATION_DATA, filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterMissingToDate() {
        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/25/20");
        reportGenerator.filter(LOCATION_DATA, filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterInvalidFromDate() {
        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "abc",
                "toDate", "3/25/20");
        reportGenerator.filter(LOCATION_DATA, filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterInvalidToDate() {
        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/20/20",
                "toDate", "/20");
        reportGenerator.filter(LOCATION_DATA, filters);
    }

    @Test
    public void testTotalCalculation() throws IOException {
        final Location usa = ImmutableLocation.builder()
                .latitude(37.0902F)
                .longitude(-95.7129F)
                .country("US")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableMap.of(
                        "3/15/20", 100,
                        "3/16/20", 120,
                        "3/17/20", 150,
                        "3/18/20", 165,
                        "3/19/20", 150))
                .build();
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData()).thenReturn(ImmutableList.of(usa));

        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/15/20",
                "toDate", "3/29/20");
        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(filters);
        assertThat(dailyConfirmedCasesDeltaReport, is(notNullValue()));
        assertThat(dailyConfirmedCasesDeltaReport.getCurrentTotalConfirmed(), is(685));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData();
    }

    @Test
    public void testDeltaCalculations() throws IOException, ParseException {
        final Location usa = ImmutableLocation.builder()
                .latitude(37.0902F)
                .longitude(-95.7129F)
                .country("US")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableMap.of(
                        "3/15/20", 100,
                        "3/16/20", 120,
                        "3/17/20", 150,
                        "3/18/20", 165,
                        "3/19/20", 150))
                .build();
        final Location ita = ImmutableLocation.builder()
                .latitude(37.0902F)
                .longitude(-95.7129F)
                .country("US")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableMap.of(
                        "3/15/20", 24747,
                        "3/16/20", 27980,
                        "3/17/20", 31506,
                        "3/18/20", 35713,
                        "3/19/20", 41035))
                .build();
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData()).thenReturn(ImmutableList.of(usa, ita));

        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/15/20",
                "toDate", "3/29/20");
        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(filters);
        assertThat(dailyConfirmedCasesDeltaReport, is(notNullValue()));

        final List<Location> confirmedCasesDeltas = dailyConfirmedCasesDeltaReport.getConfirmedCasesDeltas();
        assertThat(confirmedCasesDeltas.get(0).getDateCountData(), is(ImmutableMap.of(
                DATE_FORMAT.parse("3/15/20"), 0,
                DATE_FORMAT.parse("3/16/20"), 20,
                DATE_FORMAT.parse("3/17/20"), 30,
                DATE_FORMAT.parse("3/18/20"), 15,
                DATE_FORMAT.parse("3/19/20"), -15)));
        assertThat(confirmedCasesDeltas.get(1).getDateCountData(), is(ImmutableMap.of(
                DATE_FORMAT.parse("3/15/20"), 0,
                DATE_FORMAT.parse("3/16/20"), 3233,
                DATE_FORMAT.parse("3/17/20"), 3526,
                DATE_FORMAT.parse("3/18/20"), 4207,
                DATE_FORMAT.parse("3/19/20"), 5322)));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData();
    }
}
