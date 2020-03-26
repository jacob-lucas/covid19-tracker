package com.jacoblucas.covid19tracker.reports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.adapters.Covid19Adapter;
import com.jacoblucas.covid19tracker.models.ImmutablePair;
import com.jacoblucas.covid19tracker.models.Locations;
import com.jacoblucas.covid19tracker.models.Pair;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportGeneratorTest extends TestBase {
    private static Locations US_WASHINGTON;

    @Mock private Covid19Adapter mockCovid19Adapter;

    private ReportGenerator reportGenerator;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        US_WASHINGTON = OBJECT_MAPPER.readValue(InputReader.readAll("v2-locations-us-wa.json"), new TypeReference<Locations>() {});
    }

    @Before
    public void setUp() {
        reportGenerator = new ReportGenerator(mockCovid19Adapter);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockCovid19Adapter);
    }

    @Test
    public void testDeltaCalculations() throws IOException {
        final Map<String, String> filters = ImmutableMap.of(
                "province", "Washington",
                "country_code", "US",
                "timelines", "1",
                "source", "jhu");
        when(mockCovid19Adapter.getLocations(filters)).thenReturn(US_WASHINGTON);

        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(filters);
        assertThat(dailyConfirmedCasesDeltaReport, is(notNullValue()));

        final List<Pair<Instant, Integer>> confirmedCasesDeltas = dailyConfirmedCasesDeltaReport.getConfirmedCasesDeltas();
        assertThat(confirmedCasesDeltas, is(ImmutableList.of(
                ImmutablePair.of(Instant.parse("2020-03-10T00:00:00Z"), 267),
                ImmutablePair.of(Instant.parse("2020-03-11T00:00:00Z"), 99),
                ImmutablePair.of(Instant.parse("2020-03-12T00:00:00Z"), 76),
                ImmutablePair.of(Instant.parse("2020-03-13T00:00:00Z"), 126),
                ImmutablePair.of(Instant.parse("2020-03-14T00:00:00Z"), 4),
                ImmutablePair.of(Instant.parse("2020-03-15T00:00:00Z"), 71),
                ImmutablePair.of(Instant.parse("2020-03-16T00:00:00Z"), 261),
                ImmutablePair.of(Instant.parse("2020-03-17T00:00:00Z"), 172),
                ImmutablePair.of(Instant.parse("2020-03-18T00:00:00Z"), -62),
                ImmutablePair.of(Instant.parse("2020-03-19T00:00:00Z"), 362),
                ImmutablePair.of(Instant.parse("2020-03-20T00:00:00Z"), 148),
                ImmutablePair.of(Instant.parse("2020-03-21T00:00:00Z"), 269))));

        verify(mockCovid19Adapter, times(1)).getLocations(filters);
    }
}
