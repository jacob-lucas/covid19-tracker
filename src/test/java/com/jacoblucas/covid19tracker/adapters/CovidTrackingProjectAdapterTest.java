package com.jacoblucas.covid19tracker.adapters;

import com.google.common.collect.ImmutableList;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.ctp.ImmutableStateMetadata;
import com.jacoblucas.covid19tracker.models.ctp.ImmutableStateSummary;
import com.jacoblucas.covid19tracker.models.ctp.ImmutableUnitedStatesSummary;
import com.jacoblucas.covid19tracker.models.ctp.StateMetadata;
import com.jacoblucas.covid19tracker.models.ctp.StateSummary;
import com.jacoblucas.covid19tracker.models.ctp.UnitedStatesSummary;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CovidTrackingProjectAdapterTest extends TestBase {

    @Mock private HttpClient mockHttpClient;

    private CovidTrackingProjectAdapter adapter;

    @Before
    public void setUp() {
        adapter = new CovidTrackingProjectAdapter(mockHttpClient, "", OBJECT_MAPPER);
    }

    @After
    public void tearDown() throws IOException {
        verify(mockHttpClient, times(1)).get(anyString());
        verifyNoMoreInteractions(mockHttpClient);
    }

    @Test
    public void testGetCurrentUnitedStatesSummary() throws IOException {
        final String testData = InputReader.readAll("ctp/us/current.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final UnitedStatesSummary summary = adapter.getCurrentUnitedStatesSummary();

        final UnitedStatesSummary expected = ImmutableUnitedStatesSummary.builder()
                .states(56)
                .date(20200802)
                .death(147134)
                .deathIncrease(515)
                .totalTestResults(56812162)
                .totalTestResultsIncrease(725902)
                .positive(4644565)
                .positiveIncrease(48694)
                .build();

        assertThat(summary, is(expected));
    }

    @Test
    public void testGetHistoricUnitedStatesSummary() throws IOException {
        final String testData = InputReader.readAll("ctp/us/daily.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final List<UnitedStatesSummary> summary = adapter.getHistoricUnitedStatesSummary();

        final UnitedStatesSummary expected1 = ImmutableUnitedStatesSummary.builder()
                .states(54)
                .date(20200731)
                .death(145447)
                .deathIncrease(1308)
                .totalTestResults(55372983)
                .totalTestResultsIncrease(718956)
                .positive(4535607)
                .positiveIncrease(67503)
                .build();
        final UnitedStatesSummary expected2 = ImmutableUnitedStatesSummary.builder()
                .states(55)
                .date(20200801)
                .death(146619)
                .deathIncrease(1172)
                .totalTestResults(56086260)
                .totalTestResultsIncrease(713277)
                .positive(4595871)
                .positiveIncrease(60264)
                .build();
        final UnitedStatesSummary expected3 = ImmutableUnitedStatesSummary.builder()
                .states(56)
                .date(20200802)
                .death(147134)
                .deathIncrease(515)
                .totalTestResults(56812162)
                .totalTestResultsIncrease(725902)
                .positive(4644565)
                .positiveIncrease(48694)
                .build();

        assertThat(summary, is(ImmutableList.of(expected1, expected2, expected3)));
    }

    @Test
    public void testGetStateMetadata() throws IOException {
        final String testData = InputReader.readAll("ctp/states/info.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final Map<String, StateMetadata> metadata = adapter.getStateMetadata();

        final StateMetadata expected1 = ImmutableStateMetadata.builder()
                .name("Washington")
                .state("WA")
                .covid19Site("https://www.doh.wa.gov/Emergencies/NovelCoronavirusOutbreak2020COVID19/DataDashboard")
                .notes("Washington reports confirmed cases, laboratory tests, and deaths as of the previous day. \n\nWashington did not report new negative test results between March 31 and April 15.\n\nOn April 18, data cleaning removed 190 confirmed cases that were discovered to be out of state residents tested in Washington labs. \n\nAs of April 27, Washington is reporting people tested. \n\nOn June 18, due to a previous mistake in reporting negative cases, Washington state revised down the total number of tests. Additionally, Washington state removed seven deaths from its counts where, though the individual who died tested positive, COVID was not a contributing cause in their death.\n\nOn June 22, we updated historic numbers of cases and tests from data on Washington's dashboard. This update will temporarily cause an artificial decline in cases and numbers between June 21 and June 22. \n\nOn July 11, WA revised its hospitalizations down by 3 from 4665 to 4662.\n\nOn July 14, Washington state removed 39 deaths from its counts where, though the individual who died tested positive, COVID was not a contributing cause in their death. ")
                .build();
        final StateMetadata expected2 = ImmutableStateMetadata.builder()
                .name("California")
                .state("CA")
                .covid19Site("https://update.covid19.ca.gov")
                .notes("Before April 1, California was inconsistent in the timing of reporting so we used faster-updating county sources, we then standardized on the state data dashboard(s). This led to a drop in cases and deaths, as the state's data lags county sources.\n\nThe state reported a huge batch of negative tests on April 4 and again on April 22.\n\nAs of April 22, California now reports specimens tested instead of people tested. Because some people may be tested more than once, this number is probably higher than the number of people tested.\n\nOn July 8, California data included a backlog of data from Los Angeles County, which had not reported for several days. About 1000 of the 11,000 new cases are due to this spike in reporting.\n\nFrom June 29 to July 5, California made a few revisions to daily case numbers, on July 17 we updated the values based on the most recent data from their dashboard.\n\nStarting July 23, California has reported that its current hospitalization and ICU numbers are incomplete due to its transition to the HHS reporting system. We carried over hospitalization figures until July 28. We will backfill the complete numbers for July 23 - July 28 when reporting is back at 100%")
                .build();
        final StateMetadata expected3 = ImmutableStateMetadata.builder()
                .name("Alaska")
                .state("AK")
                .covid19Site("http://dhss.alaska.gov/dph/Epi/id/Pages/COVID-19/monitoring.aspx")
                .notes("Negatives = (Totals – Positives)\nPositives occasionally update before totals do; do not revise negatives down, keep the last calculated negative. \n\nAs of May 16, Alaska reports specimens tested; because some people may be tested more than once, this number may be higher than the number of people tested.\n\nAs of July 16, we are reporting adding non-resident cases as part of total case counts for Alaska")
                .build();

        assertThat(metadata.size(), is(3));
        assertThat(metadata.get("WA"), is(expected1));
        assertThat(metadata.get("CA"), is(expected2));
        assertThat(metadata.get("AK"), is(expected3));
    }

    @Test
    public void testGetSpecificStateMetadata() throws IOException {
        final String testData = InputReader.readAll("ctp/states/ca/info.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final StateMetadata metadata = adapter.getSpecificStateMetadata("CA");

        final StateMetadata expected = ImmutableStateMetadata.builder()
                .name("California")
                .state("CA")
                .covid19Site("https://update.covid19.ca.gov")
                .notes("Before April 1, California was inconsistent in the timing of reporting so we used faster-updating county sources, we then standardized on the state data dashboard(s). This led to a drop in cases and deaths, as the state's data lags county sources.\n\nThe state reported a huge batch of negative tests on April 4 and again on April 22.\n\nAs of April 22, California now reports specimens tested instead of people tested. Because some people may be tested more than once, this number is probably higher than the number of people tested.\n\nOn July 8, California data included a backlog of data from Los Angeles County, which had not reported for several days. About 1000 of the 11,000 new cases are due to this spike in reporting.\n\nFrom June 29 to July 5, California made a few revisions to daily case numbers, on July 17 we updated the values based on the most recent data from their dashboard.\n\nStarting July 23, California has reported that its current hospitalization and ICU numbers are incomplete due to its transition to the HHS reporting system. We carried over hospitalization figures until July 28. We will backfill the complete numbers for July 23 - July 28 when reporting is back at 100%")
                .build();

        assertThat(metadata, is(expected));
    }

    @Test
    public void testGetCurrentValuesForAllStates() throws IOException {
        final String testData = InputReader.readAll("ctp/states/current.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final Map<String, StateSummary> stateSummaryMap = adapter.getCurrentValuesForAllStates();

        final StateSummary expected1 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200802)
                .death(1592)
                .deathIncrease(28)
                .totalTestResults(1001528)
                .totalTestResultsIncrease(27874)
                .positive(57541)
                .positiveIncrease(1738)
                .build();
        final StateSummary expected2 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200802)
                .death(9356)
                .deathIncrease(132)
                .totalTestResults(8035975)
                .totalTestResultsIncrease(149388)
                .positive(509162)
                .positiveIncrease(9032)
                .build();
        final StateSummary expected3 = ImmutableStateSummary.builder()
                .state("OR")
                .date(20200802)
                .death(326)
                .deathIncrease(1)
                .totalTestResults(413657)
                .totalTestResultsIncrease(4041)
                .positive(19097)
                .positiveIncrease(280)
                .build();

        assertThat(stateSummaryMap.size(), is(3));
        assertThat(stateSummaryMap.get("WA"), is(expected1));
        assertThat(stateSummaryMap.get("CA"), is(expected2));
        assertThat(stateSummaryMap.get("OR"), is(expected3));
    }

    @Test
    public void testGetCurrentValuesForState() throws IOException {
        final String testData = InputReader.readAll("ctp/states/ca/current.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final StateSummary summary = adapter.getCurrentValuesForState("ca");

        final StateSummary expected = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200802)
                .death(9356)
                .deathIncrease(132)
                .totalTestResults(8035975)
                .totalTestResultsIncrease(149388)
                .positive(509162)
                .positiveIncrease(9032)
                .build();

        assertThat(summary, is(expected));
    }

    @Test
    public void testGetHistoricValuesForAllStatesSortByDate() throws IOException {
        final String testData = InputReader.readAll("ctp/states/daily.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final Map<Integer, List<StateSummary>> stateSummaryMap = adapter.getHistoricValuesForAllStates(StateSummary::getDate);

        final StateSummary expectedCA1 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200802)
                .death(9356)
                .deathIncrease(132)
                .totalTestResults(8035975)
                .totalTestResultsIncrease(149388)
                .positive(509162)
                .positiveIncrease(9032)
                .build();
        final StateSummary expectedCA2 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200801)
                .death(9224)
                .deathIncrease(219)
                .totalTestResults(7886587)
                .totalTestResultsIncrease(75546)
                .positive(500130)
                .positiveIncrease(6542)
                .build();
        final StateSummary expectedCA3 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200731)
                .death(9005)
                .deathIncrease(96)
                .totalTestResults(7811041)
                .totalTestResultsIncrease(177201)
                .positive(493588)
                .positiveIncrease(8086)
                .build();

        final StateSummary expectedWA1 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200802)
                .death(1592)
                .deathIncrease(28)
                .totalTestResults(1001528)
                .totalTestResultsIncrease(27874)
                .positive(57541)
                .positiveIncrease(1738)
                .build();
        final StateSummary expectedWA2 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200801)
                .death(1564)
                .deathIncrease(0)
                .totalTestResults(973654)
                .totalTestResultsIncrease(0)
                .positive(55803)
                .positiveIncrease(0)
                .build();
        final StateSummary expectedWA3 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200731)
                .death(1564)
                .deathIncrease(9)
                .totalTestResults(973654)
                .totalTestResultsIncrease(15347)
                .positive(55803)
                .positiveIncrease(818)
                .build();

        assertThat(stateSummaryMap.size(), is(3));
        assertThat(stateSummaryMap.get(20200802), is(ImmutableList.of(expectedCA1, expectedWA1)));
        assertThat(stateSummaryMap.get(20200801), is(ImmutableList.of(expectedCA2, expectedWA2)));
        assertThat(stateSummaryMap.get(20200731), is(ImmutableList.of(expectedCA3, expectedWA3)));
    }

    @Test
    public void testGetHistoricValuesForAllStatesSortByState() throws IOException {
        final String testData = InputReader.readAll("ctp/states/daily.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final Map<String, List<StateSummary>> stateSummaryMap = adapter.getHistoricValuesForAllStates(StateSummary::getState);

        final StateSummary expectedCA1 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200802)
                .death(9356)
                .deathIncrease(132)
                .totalTestResults(8035975)
                .totalTestResultsIncrease(149388)
                .positive(509162)
                .positiveIncrease(9032)
                .build();
        final StateSummary expectedCA2 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200801)
                .death(9224)
                .deathIncrease(219)
                .totalTestResults(7886587)
                .totalTestResultsIncrease(75546)
                .positive(500130)
                .positiveIncrease(6542)
                .build();
        final StateSummary expectedCA3 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200731)
                .death(9005)
                .deathIncrease(96)
                .totalTestResults(7811041)
                .totalTestResultsIncrease(177201)
                .positive(493588)
                .positiveIncrease(8086)
                .build();

        final StateSummary expectedWA1 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200802)
                .death(1592)
                .deathIncrease(28)
                .totalTestResults(1001528)
                .totalTestResultsIncrease(27874)
                .positive(57541)
                .positiveIncrease(1738)
                .build();
        final StateSummary expectedWA2 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200801)
                .death(1564)
                .deathIncrease(0)
                .totalTestResults(973654)
                .totalTestResultsIncrease(0)
                .positive(55803)
                .positiveIncrease(0)
                .build();
        final StateSummary expectedWA3 = ImmutableStateSummary.builder()
                .state("WA")
                .date(20200731)
                .death(1564)
                .deathIncrease(9)
                .totalTestResults(973654)
                .totalTestResultsIncrease(15347)
                .positive(55803)
                .positiveIncrease(818)
                .build();

        assertThat(stateSummaryMap.size(), is(2));
        assertThat(stateSummaryMap.get("CA"), is(ImmutableList.of(expectedCA3, expectedCA2, expectedCA1)));
        assertThat(stateSummaryMap.get("WA"), is(ImmutableList.of(expectedWA3, expectedWA2, expectedWA1)));
    }

    @Test
    public void testGetHistoricValuesForState() throws IOException {
        final String testData = InputReader.readAll("ctp/states/ca/daily.json", "\n");
        when(mockHttpClient.get(anyString())).thenReturn(testData);

        final List<StateSummary> summaries = adapter.getHistoricValuesForState("CA");

        final StateSummary expectedCA1 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200802)
                .death(9356)
                .deathIncrease(132)
                .totalTestResults(8035975)
                .totalTestResultsIncrease(149388)
                .positive(509162)
                .positiveIncrease(9032)
                .build();
        final StateSummary expectedCA2 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200801)
                .death(9224)
                .deathIncrease(219)
                .totalTestResults(7886587)
                .totalTestResultsIncrease(75546)
                .positive(500130)
                .positiveIncrease(6542)
                .build();
        final StateSummary expectedCA3 = ImmutableStateSummary.builder()
                .state("CA")
                .date(20200731)
                .death(9005)
                .deathIncrease(96)
                .totalTestResults(7811041)
                .totalTestResultsIncrease(177201)
                .positive(493588)
                .positiveIncrease(8086)
                .build();

        assertThat(summaries, is(ImmutableList.of(expectedCA3, expectedCA2, expectedCA1)));
    }
}
