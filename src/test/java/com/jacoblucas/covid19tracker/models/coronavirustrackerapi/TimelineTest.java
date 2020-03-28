package com.jacoblucas.covid19tracker.models.coronavirustrackerapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TimelineTest extends TestBase {
    private static String JSON;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        JSON = InputReader.readAll("v2-locations-specific.json");
    }

    @Test
    public void testDeserialize() throws IOException {
        final Locations locations = OBJECT_MAPPER.readValue(JSON, new TypeReference<Locations>() {});
        assertThat(locations, is(notNullValue()));

        final List<Map<String, Timeline>> timelines = locations.getLocations().stream()
                .map(Location::getTimelines)
                .collect(Collectors.toList());
        assertThat(timelines.isEmpty(), is(false));

        final Map<String, Timeline> kingCountyWA = locations.getLocations().stream()
                .filter(loc -> loc.getProvince().equals("King County, WA"))
                .findFirst()
                .map(Location::getTimelines)
                .orElse(ImmutableMap.of());
        assertThat(kingCountyWA.isEmpty(), is(false));
        assertThat(kingCountyWA.containsKey(Metrics.CONFIRMED), is(true));
        assertThat(kingCountyWA.containsKey(Metrics.DEATHS), is(true));
        assertThat(kingCountyWA.containsKey(Metrics.RECOVERED), is(true));

        final Timeline timeline = kingCountyWA.get(Metrics.CONFIRMED);
        assertThat(timeline.getLatest(), is(0));
        assertThat(timeline.getTimeline().size(), is(60));
        assertThat(timeline.getTimeline().get(Instant.parse("2020-03-09T00:00:00Z")), is(83));
    }
}
