package com.jacoblucas.covid19tracker.models;

public enum LocationStatus {
    OUTBREAKING,
    FLATTENING,
    RECOVERING,
    STABLE;

    public static LocationStatus of(final float gradient) {
        if (gradient > 1) {
            return OUTBREAKING;
        } else if (gradient > 0) {
            return FLATTENING;
        } else if (gradient < 0) {
            return RECOVERING;
        } else {
            return STABLE;
        }
    }
}
