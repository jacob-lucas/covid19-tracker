package com.jacoblucas.covid19tracker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(allParameters = true)
@JsonDeserialize(as = ImmutableCoordinates.class)
public abstract class Coordinates {
    public abstract float getLatitude();

    public abstract float getLongitude();
}
