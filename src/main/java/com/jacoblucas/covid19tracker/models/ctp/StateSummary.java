package com.jacoblucas.covid19tracker.models.ctp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ImmutableStateSummary.class)
public abstract class StateSummary extends Summary {
    public abstract String getState();
}
