package com.jacoblucas.covid19tracker.models.ctp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableUnitedStatesReport.class)
public abstract class UnitedStatesReport {
    public abstract Summary getCurrentData();

    public abstract Map<String, Summary> getCurrentDataByState();

    public abstract Map<String, StateMetadata> getStateMetadata();
}
