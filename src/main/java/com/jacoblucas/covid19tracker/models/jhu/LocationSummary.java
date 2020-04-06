package com.jacoblucas.covid19tracker.models.jhu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacoblucas.covid19tracker.models.Coordinates;
import com.jacoblucas.covid19tracker.models.ImmutableCoordinates;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class LocationSummary {
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

    public abstract LocationDataType getLocationDataType();

    public abstract int getCount();

    public abstract Date getUpdatedAt();

    public static LocationSummary generate(final Location location) {

        // summary count is the most recent location data
        final Map<Date, Integer> dateCountData = location.getDateCountData();
        final Optional<Date> mostRecent = dateCountData.keySet().stream().max(Comparator.naturalOrder());
        if (!mostRecent.isPresent()) {
            throw new IllegalArgumentException(String.format("Missing date count data - cannot summarise %s", location));
        }

        final Date updatedAt = mostRecent.get();
        return ImmutableLocationSummary.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .country(location.getCountry())
                .state(location.getState())
                .locationDataType(location.getLocationDataType())
                .count(dateCountData.get(updatedAt))
                .updatedAt(updatedAt)
                .build();
    }
}
