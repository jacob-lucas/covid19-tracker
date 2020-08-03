package com.jacoblucas.covid19tracker.models.ctp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ImmutableUnitedStatesSummary.class)
public abstract class UnitedStatesSummary extends Summary {
    // Number of states and territories included in the US dataset for this day.
    public abstract int getStates();
}
