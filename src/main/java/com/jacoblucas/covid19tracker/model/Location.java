package com.jacoblucas.covid19tracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.neovisionaries.i18n.CountryCode;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableLocation.class)
// Models a specific location used in the locations API - https://coronavirus-tracker-api.herokuapp.com/v2/locations
public abstract class Location {
    public abstract int getId();

    @Value.Derived
    public String getCountry() {
        return getCountryCode().getName();
    }

    @JsonProperty("country_code")
    public abstract CountryCode getCountryCode();

    public abstract String getProvince();

    @JsonProperty("last_updated")
    public abstract Instant getLastUpdated();

    public abstract Coordinates getCoordinates();

    public abstract Map<String, Integer> getLatest();

    public abstract Map<String, Timeline> getTimelines();
}
