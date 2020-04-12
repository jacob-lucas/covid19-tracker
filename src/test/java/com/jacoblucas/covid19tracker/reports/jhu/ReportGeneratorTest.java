package com.jacoblucas.covid19tracker.reports.jhu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
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
        LOCATION_DATA = Location.parse(rawTsd, LocationDataType.CONFIRMED_CASES);
    }

    @Before
    public void setUp() throws IOException {
        final Location usLocationData = Location.aggregateByCountry(
                LOCATION_DATA.stream()
                        .filter(loc -> loc.getCountry().equals("US"))
                        .collect(Collectors.toList()));

        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData(LocationDataType.CONFIRMED_CASES)).thenReturn(ImmutableList.of(usLocationData));
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
            return fromStr.equals("03/19/20");
        }), is(true));
        assertThat(filtered.stream().allMatch(loc -> {
            final String toStr = DATE_FORMAT.format(loc.getDateCountData().keySet().stream().max(Comparator.naturalOrder()).get());
            return toStr.equals("03/25/20");
        }), is(true));
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
    public void testEmptyFilter() {
        final List<Location> filtered = reportGenerator.filter(LOCATION_DATA, ImmutableMap.of());
        assertThat(filtered, is(LOCATION_DATA));
    }

    @Test
    public void testTotalCalculationWithDates() throws IOException {
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData(LocationDataType.CONFIRMED_CASES)).thenReturn(LOCATION_DATA);

        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/15/20",
                "toDate", "3/19/20",
                "country", "US");
        final DailyNewCasesReport dailyNewCasesReport = reportGenerator.generateDailyNewCasesReport(filters);
        assertThat(dailyNewCasesReport, is(notNullValue()));
        assertThat(dailyNewCasesReport.getTotal(), is(10950));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData(LocationDataType.CONFIRMED_CASES);
    }

    @Test
    public void testTotalCalculationNoDates() throws IOException {
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData(LocationDataType.CONFIRMED_CASES)).thenReturn(LOCATION_DATA);

        final Map<String, String> filters = ImmutableMap.of("country", "US");
        final DailyNewCasesReport dailyNewCasesReport = reportGenerator.generateDailyNewCasesReport(filters);
        assertThat(dailyNewCasesReport, is(notNullValue()));
        assertThat(dailyNewCasesReport.getTotal(), is(65777));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData(LocationDataType.CONFIRMED_CASES);
    }

    @Test
    public void testDeltaCalculations() throws IOException, ParseException {
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData(LocationDataType.CONFIRMED_CASES)).thenReturn(LOCATION_DATA);

        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/15/20",
                "toDate", "3/19/20",
                "country", "US");

        final DailyNewCasesReport dailyNewCasesReport = reportGenerator.generateDailyNewCasesReport(filters);
        assertThat(dailyNewCasesReport, is(notNullValue()));

        final List<Location> confirmedCasesDeltas = dailyNewCasesReport.getDailyNewCases();
        assertThat(confirmedCasesDeltas.get(0).getDateCountData(), is(ImmutableMap.of(
                DATE_FORMAT.parse("3/15/20"), 772,
                DATE_FORMAT.parse("3/16/20"), 1133,
                DATE_FORMAT.parse("3/17/20"), 1789,
                DATE_FORMAT.parse("3/18/20"), 1362,
                DATE_FORMAT.parse("3/19/20"), 5894)));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData(LocationDataType.CONFIRMED_CASES);
    }

    @Test
    public void testAggregatesWhenStateNotFiltered() throws IOException, ParseException {
        when(mockJohnsHopkinsCovid19Adapter.getAllLocationData(LocationDataType.CONFIRMED_CASES)).thenReturn(LOCATION_DATA);

        final Map<String, String> filters = ImmutableMap.of(
                "fromDate", "3/15/20",
                "toDate", "3/19/20",
                "country", "Australia");

        final DailyNewCasesReport dailyNewCasesReport = reportGenerator.generateDailyNewCasesReport(filters);
        assertThat(dailyNewCasesReport, is(notNullValue()));

        assertThat(dailyNewCasesReport.getTotal(), is(431));

        final List<Location> confirmedCasesDeltas = dailyNewCasesReport.getDailyNewCases();
        assertThat(confirmedCasesDeltas.size(), is(1));
        assertThat(confirmedCasesDeltas.get(0).getDateCountData(), is(ImmutableMap.of(
                DATE_FORMAT.parse("3/15/20"), 47,
                DATE_FORMAT.parse("3/16/20"), 80,
                DATE_FORMAT.parse("3/17/20"), 75,
                DATE_FORMAT.parse("3/18/20"), 116,
                DATE_FORMAT.parse("3/19/20"), 113)));

        verify(mockJohnsHopkinsCovid19Adapter, times(1)).getAllLocationData(LocationDataType.CONFIRMED_CASES);
    }
}
