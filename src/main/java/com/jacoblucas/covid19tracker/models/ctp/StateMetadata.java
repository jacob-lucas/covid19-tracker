package com.jacoblucas.covid19tracker.models.ctp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(redactedMask = "###REDACTED###")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ImmutableStateMetadata.class)
public abstract class StateMetadata {
    public abstract String getCovid19Site();

    public abstract String getName();

    public abstract String getState();

    @Value.Redacted
    public abstract Optional<String> getNotes();
}
