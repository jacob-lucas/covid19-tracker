package com.jacoblucas.covid19tracker.adapters;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.jacoblucas.covid19tracker.adapters.Covid19Adapter.BASE_URL;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Covid19AdapterTest extends TestBase {
    private static String JSON;

    @Mock private HttpClient mockHttpClient;

    private Covid19Adapter covid19Adapter;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        JSON = InputReader.readAll("v2-locations-response.json");
    }

    @Before
    public void setUp() {
        covid19Adapter = new Covid19Adapter(mockHttpClient);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockHttpClient);
    }

    @Test
    public void testGetAllLocations() throws IOException {
        final ImmutableMap<String, String> querystringMap = ImmutableMap.of("timelines", "1");
        when(mockHttpClient.get(BASE_URL + "locations", querystringMap)).thenReturn(JSON);

        covid19Adapter.getAllLocations(true);
        verify(mockHttpClient, times(1)).get(BASE_URL + "locations", querystringMap);
    }

    @Test(expected = IOException.class)
    public void testGetAllLocationsThrowsException() throws IOException {
        final ImmutableMap<String, String> querystringMap = ImmutableMap.of("timelines", "1");
        when(mockHttpClient.get(BASE_URL + "locations", querystringMap)).thenThrow(new IOException("error!"));

        try {
            covid19Adapter.getAllLocations(true);
        } finally {
            verify(mockHttpClient, times(1)).get(BASE_URL + "locations", querystringMap);
        }
    }

    @Test
    public void testGetLocationsByCountryCode() throws IOException {
        final ImmutableMap<String, String> querystringMap = ImmutableMap.of("timelines", "1", "country_code", "US");
        when(mockHttpClient.get(BASE_URL + "locations", querystringMap)).thenReturn(JSON);

        covid19Adapter.getLocationsByCountry("US", true);
        verify(mockHttpClient, times(1)).get(BASE_URL + "locations", querystringMap);
    }

    @Test(expected = IOException.class)
    public void testGetLocationsByCountryCodeThrowsException() throws IOException {
        final ImmutableMap<String, String> querystringMap = ImmutableMap.of("timelines", "1", "country_code", "US");
        when(mockHttpClient.get(BASE_URL + "locations", querystringMap)).thenThrow(new IOException("error!"));

        try {
            covid19Adapter.getLocationsByCountry("US", true);
        } finally {
            verify(mockHttpClient, times(1)).get(BASE_URL + "locations", querystringMap);
        }
    }

    @Test
    public void testGetLocation() throws IOException {
        final int id = 39;
        final String json = InputReader.readAll("v2-location-response.json");
        when(mockHttpClient.get(BASE_URL + "locations/" + id)).thenReturn(json);

        covid19Adapter.getLocation(id);
        verify(mockHttpClient, times(1)).get(BASE_URL + "locations/" + id);
    }

    @Test(expected = IOException.class)
    public void testGetLocationThrowsException() throws IOException {
        final int id = 39;
        when(mockHttpClient.get(BASE_URL + "locations/" + id)).thenThrow(new IOException("error!"));

        try {
            covid19Adapter.getLocation(id);
        } finally {
            verify(mockHttpClient, times(1)).get(BASE_URL + "locations/" + id);
        }
    }
}
