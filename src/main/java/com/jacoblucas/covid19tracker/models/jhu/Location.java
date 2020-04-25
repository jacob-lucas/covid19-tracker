package com.jacoblucas.covid19tracker.models.jhu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.models.Coordinates;
import com.jacoblucas.covid19tracker.models.ImmutableCoordinates;
import org.immutables.value.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter.DELIMITER;

@Value.Immutable
public abstract class Location {
    public abstract float getLatitude();

    public abstract float getLongitude();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Coordinates getCoordinates() {
        return ImmutableCoordinates.of(getLatitude(), getLongitude());
    }

    public abstract String getCountry();

    public abstract Optional<String> getState();

    public abstract Optional<String> getCounty();

    public abstract LocationDataType getLocationDataType();

    public abstract Map<String, Integer> getRawCountData();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Map<Date, Integer> getDateCountData() {
        final DateFormat dateFormat = new SimpleDateFormat(JohnsHopkinsCovid19Adapter.DATE_FORMAT);
        final Map<Date, Integer> data = Maps.newTreeMap();
        for (Map.Entry<String, Integer> entry : getRawCountData().entrySet()) {
            try {
                data.put(dateFormat.parse(entry.getKey()), entry.getValue());
            } catch (ParseException e) {
                // do nothing
            }
        }
        return data;
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public int getTotal() {
        return getDateCountData().values()
                .stream()
                .reduce(0, Integer::sum);
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Date getUpdatedAt() {
        return getDateCountData().keySet().stream().max(Comparator.naturalOrder()).orElse(new Date());
    }

    public static Location aggregateByState(final List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException("Must provided a non-null/non-empty list for aggregation");
        }

        final List<String> states = locations.stream()
                .map(Location::getState)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
        if (states.size() > 1) {
            throw new IllegalArgumentException("Cannot aggregate locations from different states");
        }

        final List<LocationDataType> locationDataType = locations.stream()
                .map(Location::getLocationDataType)
                .distinct()
                .collect(Collectors.toList());

        final List<Map<String, Integer>> rawDataMaps = locations.stream()
                .map(Location::getRawCountData)
                .collect(Collectors.toList());

        final Map<String, Integer> aggregatedData = rawDataMaps.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

        return ImmutableLocation.builder()
                .latitude(locations.get(0).getLatitude())
                .longitude(locations.get(0).getLongitude())
                .country(locations.get(0).getCountry())
                .state(states.get(0))
                .locationDataType(locationDataType.get(0))
                .rawCountData(aggregatedData)
                .build();
    }

    public static Location aggregateByCountry(final List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException("Must provided a non-null/non-empty list for aggregation");
        }

        final List<String> country = locations.stream()
                .map(Location::getCountry)
                .distinct()
                .collect(Collectors.toList());
        if (country.size() > 1) {
            throw new IllegalArgumentException("Cannot aggregate locations from different countries");
        }

        final List<LocationDataType> locationDataType = locations.stream()
                .map(Location::getLocationDataType)
                .distinct()
                .collect(Collectors.toList());

        final List<Map<String, Integer>> rawDataMaps = locations.stream()
                .map(Location::getRawCountData)
                .collect(Collectors.toList());

        final Map<String, Integer> aggregatedData = rawDataMaps.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

        return ImmutableLocation.builder()
                .latitude(locations.get(0).getLatitude())
                .longitude(locations.get(0).getLongitude())
                .country(country.get(0))
                .locationDataType(locationDataType.get(0))
                .rawCountData(aggregatedData)
                .build();
    }

    public static List<Location> parse(final List<String> rawData, final LocationDataType locationDataType) {
        final String[] headers = rawData.remove(0).split(DELIMITER);
        return rawData.stream()
                .map(str -> str.split(DELIMITER))
                .map(arr -> parse(headers, arr, locationDataType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    static Optional<Location> parse(final String[] headers, final String[] arr, final LocationDataType locationDataType) {
        try {
            int stateIdx = 0;
            int countryIdx = 1;
            int latIdx = 2;
            int longIdx = 3;
            int dataIdx = 4;
            int offset = 0;

            String countryStr = arr[countryIdx];
            if (countryStr.contains("\"")) {
                final List<String> country = new ArrayList<>();
                country.add(countryStr);
                boolean foundOther = false;
                while (!foundOther) {
                    countryStr = arr[++countryIdx];
                    country.add(countryStr);
                    foundOther = countryStr.contains("\"");
                }
                countryStr = String.join(DELIMITER, country).replaceAll("\"", "");
                offset = (countryIdx - 1);
            }

            final ImmutableLocation.Builder builder = ImmutableLocation.builder()
                    .country(countryStr)
                    .latitude(Float.parseFloat(arr[latIdx + offset]))
                    .longitude(Float.parseFloat(arr[longIdx + offset]))
                    .locationDataType(locationDataType);

            final String state = arr[stateIdx];
            if (!state.isEmpty()) {
                builder.state(state);
            }

            final Map<String, Integer> data = Maps.newTreeMap();
            for (int i = dataIdx; i < headers.length; i++) {
                final int count = Integer.parseInt(arr[i + offset].trim());
                data.put(headers[i], count);
            }
            builder.rawCountData(data);

            return Optional.of(builder.build());
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

    public static List<Location> parseDetailed(final List<String> rawData, final LocationDataType locationDataType) {
        final String[] headers = rawData.remove(0).split(DELIMITER);
        return rawData.stream()
                .map(str -> str.split(DELIMITER))
                .map(arr -> parseDetailed(headers, arr, locationDataType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    static Optional<Location> parseDetailed(final String[] headers, final String[] arr, final LocationDataType locationDataType) {
        try {
            int countyIdx = 5;
            int stateIdx = 6;
            int countryIdx = 7;
            int latIdx = 8;
            int longIdx = 9;
            int dataIdx = arr.length - (headers.length-11);
            int offset = dataIdx - 11;

            final Map<String, Integer> data = Maps.newTreeMap();
            for (int i = dataIdx; i < arr.length; i++) {
                final int count = Integer.parseInt(arr[i].trim());
                data.put(headers[i-offset], count);
            }

            return Optional.of(ImmutableLocation.builder()
                    .country(arr[countryIdx])
                    .state(arr[stateIdx])
                    .county(arr[countyIdx])
                    .latitude(Float.parseFloat(arr[latIdx]))
                    .longitude(Float.parseFloat(arr[longIdx]))
                    .locationDataType(locationDataType)
                    .rawCountData(data)
                    .build());
        } catch (final Exception e) {
            return Optional.empty();
        }
    }
}
