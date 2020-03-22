package com.jacoblucas.covid19tracker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Maps;
import com.neovisionaries.i18n.CountryCode;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableLocations.class)
// Models the response of the /v2/locations API - https://coronavirus-tracker-api.herokuapp.com/v2/locations
public abstract class Locations {
    public abstract Map<String, Integer> getLatest();

    public abstract List<Location> getLocations();

    @Value.Derived
    public Map<CountryCode, Location> getLocationMap() {
        return Maps.uniqueIndex(getLocations(), Location::getCountryCode);
    }
}
