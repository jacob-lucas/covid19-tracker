package com.jacoblucas.covid19tracker.model;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.Map;

@Value.Immutable
// Models a timeline used as part of a location in the locations API - https://coronavirus-tracker-api.herokuapp.com/v2/locations
public abstract class Timeline {
    public abstract String getLabel();

    public abstract int getLatest();

    public abstract Map<Instant, Integer> getTimeline();
}
