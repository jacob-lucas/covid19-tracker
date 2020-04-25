package com.jacoblucas.covid19tracker.models.jhu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter.DELIMITER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationTest {
    private static final String HEADERS = "Province/State,Country/Region,Lat,Long,3/21/20,3/22/20,3/23/20,3/24/20,3/25/20";
    private static final String WESTERN_AUSTRALIA = "Western Australia,Australia,-31.9505,115.8605,90,120,140,175,175";
    private static final String US = ",US,37.0902,-95.7129,25489,33276,43847,53740,65778";
    private static final String SOUTH_KOREA = ",\"Korea, South\",36.0,128.0,8799,8961,8961,9037,9137";

    private static final String DETAIL_HEADERS = "UID,iso2,iso3,code3,FIPS,Admin2,Province_State,Country_Region,Lat,Long_,Combined_Key,4/16/20,4/17/20,4/18/20,4/19/20,4/20/20";
    private static final String KING_COUNTY_WA = "84053033,US,USA,840,53033.0,King,Washington,US,47.49137892,-121.8346131,\"King, Washington, US\",4697,4902,4912,5154,5174";
    private static final String PIERCE_COUNTY_WA = "84053053,US,USA,840,53053.0,Pierce,Washington,US,47.03892768,-122.14059579999999,\"Pierce, Washington, US\",1022,1086,1086,1143,1188";
    private static final String SNOHOMISH_COUNTY_WA = "84053061,US,USA,840,53061.0,Snohomish,Washington,US,48.04615983,-121.7170703,\"Snohomish, Washington, US\",2032,2056,2093,2143,2163";
    private static final String NY_COUNTY_NY = "84036061,US,USA,840,36061.0,New York,New York,US,40.76727260000001,-73.97152637,\"New York City, New York, US\",123146,127352,135572,138700,141235";

    @Test
    public void testParseWithoutState() {
        final Location expected = ImmutableLocation.builder()
                .latitude(37.0902F)
                .longitude(-95.7129F)
                .country("US")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "3/21/20", 25489,
                        "3/22/20", 33276,
                        "3/23/20", 43847,
                        "3/24/20", 53740,
                        "3/25/20", 65778))
                .build();

        final Optional<Location> location = Location.parse(HEADERS.split(DELIMITER), US.split(DELIMITER), LocationDataType.CONFIRMED_CASES);

        assertThat(location.get(), is(expected));
        assertThat(location.get().getState().isPresent(), is(false));
    }

    @Test
    public void testParseWithState() {
        final Location expected = ImmutableLocation.builder()
                .latitude(-31.9505F)
                .longitude(115.8605F)
                .country("Australia")
                .state("Western Australia")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "3/21/20", 90,
                        "3/22/20", 120,
                        "3/23/20", 140,
                        "3/24/20", 175,
                        "3/25/20", 175))
                .build();

        final Optional<Location> location = Location.parse(HEADERS.split(DELIMITER), WESTERN_AUSTRALIA.split(DELIMITER), LocationDataType.CONFIRMED_CASES);

        assertThat(location.get(), is(expected));
        assertThat(location.get().getState().isPresent(), is(true));
    }

    @Test
    public void testParseWithDelimiterInCountry() {
        final Location expected = ImmutableLocation.builder()
                .latitude(36.0F)
                .longitude(128.0F)
                .country("Korea, South")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "3/21/20", 8799,
                        "3/22/20", 8961,
                        "3/23/20", 8961,
                        "3/24/20", 9037,
                        "3/25/20", 9137))
                .build();

        final Optional<Location> location = Location.parse(HEADERS.split(DELIMITER), SOUTH_KOREA.split(DELIMITER), LocationDataType.CONFIRMED_CASES);

        assertThat(location.get(), is(expected));
        assertThat(location.get().getState().isPresent(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateByCountryForMultipleCountries() {
        final List<String> rawTsd = ImmutableList.of(US, WESTERN_AUSTRALIA);

        final List<Location> locations = rawTsd.stream()
                .map(str -> Location.parse(HEADERS.split(DELIMITER), str.split(DELIMITER), LocationDataType.CONFIRMED_CASES))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Location.aggregateByCountry(locations);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateByCountryForEmptyList() {
        Location.aggregateByCountry(ImmutableList.of());
    }

    @Test
    public void testAggregateByCountryForKnownCountry() {
        final List<String> rawTsd = ImmutableList.of(
                "Australian Capital Territory,Australia,-35.4735,149.0124,9,19,32,39,39",
                "New South Wales,Australia,-33.8688,151.2093,436,669,669,818,1029",
                "Northern Territory,Australia,-12.4634,130.8456,3,5,5,6,6",
                "Queensland,Australia,-28.0167,153.4,221,259,319,397,443",
                "South Australia,Australia,-34.9285,138.6007,67,100,134,170,170",
                "Tasmania,Australia,-41.4545,145.9707,16,22,28,28,36",
                "Victoria,Australia,-37.8136,144.9631,229,355,355,411,466",
                "Western Australia,Australia,-31.9505,115.8605,90,120,140,175,175");

        final List<Location> australianStateData = rawTsd.stream()
                .map(str -> Location.parse(HEADERS.split(DELIMITER), str.split(DELIMITER), LocationDataType.CONFIRMED_CASES))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        final Location australia = Location.aggregateByCountry(australianStateData);

        final Location expected = ImmutableLocation.builder()
                .latitude(-35.4735F)
                .longitude(149.0124F)
                .country("Australia")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "3/21/20", 1071,
                        "3/22/20", 1549,
                        "3/23/20", 1682,
                        "3/24/20", 2044,
                        "3/25/20", 2364))
                .build();

        assertThat(australia, is(expected));
    }

    @Test
    public void testParseDetailed() {
        final Location expected = ImmutableLocation.builder()
                .latitude(47.49137892F)
                .longitude(-121.8346131F)
                .country("US")
                .state("Washington")
                .county("King")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "4/16/20", 4697,
                        "4/17/20", 4902,
                        "4/18/20", 4912,
                        "4/19/20", 5154,
                        "4/20/20", 5174))
                .build();

        final Optional<Location> location = Location.parseDetailed(DETAIL_HEADERS.split(DELIMITER), KING_COUNTY_WA.split(DELIMITER), LocationDataType.CONFIRMED_CASES);

        assertThat(location.get(), is(expected));
        assertThat(location.get().getState().isPresent(), is(true));
        assertThat(location.get().getCounty().isPresent(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateByStateForMultipleStates() {
        final List<String> rawTsd = ImmutableList.of(NY_COUNTY_NY, KING_COUNTY_WA);

        final List<Location> locations = rawTsd.stream()
                .map(str -> Location.parse(HEADERS.split(DELIMITER), str.split(DELIMITER), LocationDataType.CONFIRMED_CASES))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Location.aggregateByCountry(locations);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateByStateForEmptyList() {
        Location.aggregateByState(ImmutableList.of());
    }

    @Test
    public void testAggregateByStateForKnownState() {
        final List<String> rawTsd = ImmutableList.of(KING_COUNTY_WA, PIERCE_COUNTY_WA, SNOHOMISH_COUNTY_WA);

        final List<Location> stateData = rawTsd.stream()
                .map(str -> Location.parseDetailed(DETAIL_HEADERS.split(DELIMITER), str.split(DELIMITER), LocationDataType.CONFIRMED_CASES))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        final Location washington = Location.aggregateByState(stateData);

        final Location expected = ImmutableLocation.builder()
                .latitude(47.49137892F)
                .longitude(-121.8346131F)
                .country("US")
                .state("Washington")
                .locationDataType(LocationDataType.CONFIRMED_CASES)
                .rawCountData(ImmutableSortedMap.of(
                        "4/16/20", 7751,
                        "4/17/20", 8044,
                        "4/18/20", 8091,
                        "4/19/20", 8440,
                        "4/20/20", 8525))
                .build();

        assertThat(washington, is(expected));
    }
}
