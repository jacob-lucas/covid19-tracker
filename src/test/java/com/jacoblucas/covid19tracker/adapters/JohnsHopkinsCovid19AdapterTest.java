package com.jacoblucas.covid19tracker.adapters;

import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class JohnsHopkinsCovid19AdapterTest {
    private static String RAW_TSD;

    @Mock private HttpClient mockHttpClient;

    private JohnsHopkinsCovid19Adapter adapter;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        RAW_TSD = InputReader.readAll("time_series_covid19_confirmed_global.csv", "\n");
    }

    @Before
    public void setUp() {
        adapter = new JohnsHopkinsCovid19Adapter(mockHttpClient, "");
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockHttpClient);
    }

    @Test
    public void testGetAllLocationData() throws IOException {
        when(mockHttpClient.get(anyString())).thenReturn(RAW_TSD);

        final List<Location> locations = adapter.getAllLocationData();

        assertThat(locations.size(), is(245));
        verify(mockHttpClient, times(1)).get(anyString());
    }

    @Test
    public void testGetLocationDataByCountryForUnknownCountry() throws IOException {
        when(mockHttpClient.get(anyString())).thenReturn(RAW_TSD);

        final Optional<Location> location = adapter.getLocationData("does not exist");

        assertThat(location.isPresent(), is(false));
        verify(mockHttpClient, times(1)).get(anyString());
    }

    @Test
    public void testGetLocationDataByCountryForKnownCountry() throws IOException {
        when(mockHttpClient.get(anyString())).thenReturn(RAW_TSD);

        final Optional<Location> location = adapter.getLocationData("Australia");

        assertThat(location.isPresent(), is(true));
        verify(mockHttpClient, times(1)).get(anyString());
    }
}
