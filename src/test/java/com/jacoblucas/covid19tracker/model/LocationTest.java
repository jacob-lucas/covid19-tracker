package com.jacoblucas.covid19tracker.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.utils.InputReader;
import com.neovisionaries.i18n.CountryCode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationTest extends TestBase {
    private static String JSON;

    @BeforeClass
    public static void setUpSuite() throws IOException {
        JSON = InputReader.readAll("v2-locations-response.json");
    }

    @Test
    public void testDeserialize() throws IOException {
        final Locations locations = OBJECT_MAPPER.readValue(JSON, new TypeReference<Locations>() {});
        assertThat(locations.getLocationMap().size(), is(2));

        final Location norway = ImmutableLocation.builder()
                .id(39)
                .countryCode(CountryCode.NO)
                .province("")
                .lastUpdated(Instant.parse("2020-03-21T06:59:11.315422Z"))
                .coordinates(ImmutableCoordinates.of(60.472F, 8.4689F))
                .latest(ImmutableMap.of(
                        Metrics.CONFIRMED, 1463,
                        Metrics.DEATHS, 3,
                        Metrics.RECOVERED, 1))
                .build();

        final Location result = locations.getLocationMap().get(CountryCode.NO);
        assertThat(result, is(norway));
    }

}