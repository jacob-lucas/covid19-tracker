package com.jacoblucas.covid19tracker.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableLocations.class)
// Models the response of the /v2/locations API - https://coronavirus-tracker-api.herokuapp.com/v2/locations
public abstract class Locations {
    public abstract Map<String, Integer> getLatest();

    public abstract List<Location> getLocations();
}
