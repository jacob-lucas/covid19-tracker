package com.jacoblucas.covid19tracker.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableTimeline.class)
// Models a timeline used as part of a location in the locations API - https://coronavirus-tracker-api.herokuapp.com/v2/locations
public abstract class Timeline {
    public abstract int getLatest();

    public abstract Map<Instant, Integer> getTimeline();
}
