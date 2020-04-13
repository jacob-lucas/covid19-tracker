package com.jacoblucas.covid19tracker.models;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Trend {
    public abstract String getLocation();

    public abstract List<Integer> getWindows();

    public abstract int getWindowSize();

    @Value.Derived
    public int getWindowCount() {
        return getWindows().size();
    }

    @Value.Derived
    public float getGradient() {
        final int dx = getWindowCount();
        final int dy = getWindows().get(0) - getWindows().get(dx-1);
        return (float) dy / dx;
    }

    @Value.Derived
    public LocationStatus getLocationStatus() {
        return LocationStatus.of(getGradient());
    }
}
