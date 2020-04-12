package com.jacoblucas.covid19tracker.models.jhu;

import java.util.Arrays;

public enum LocationDataType {
    CONFIRMED_CASES,
    DEATHS;

    public static LocationDataType of(final String name) {
        return Arrays.stream(LocationDataType.values())
                .filter(ldt -> ldt.name().equals(name.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid location data type requested: \"%s\"", name)));
    }
}
