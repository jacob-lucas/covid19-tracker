package com.jacoblucas.covid19tracker.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationsTest extends TestBase {
    private static String JSON;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        JSON = InputReader.readAll("v2-locations-response.json");
    }

    @Test
    public void testDeserialize() throws IOException {
        final Locations locations = OBJECT_MAPPER.readValue(JSON, new TypeReference<Locations>() {});

        final Map<String, Integer> latest = locations.getLatest();
        assertThat(latest.get(Metrics.CONFIRMED), is(272166));
        assertThat(latest.get(Metrics.DEATHS), is(11299));
        assertThat(latest.get(Metrics.RECOVERED), is(87256));

        final List<Location> locationList = locations.getLocations();
        assertThat(locationList.size(), is(2));
    }

}
