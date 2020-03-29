package com.jacoblucas.covid19tracker.reports.jhu.filters;

import com.jacoblucas.covid19tracker.models.jhu.Location;

import java.util.function.Predicate;

public class CountryFilter implements Predicate<Location> {
    private final String country;

    public CountryFilter(final String country) {
        this.country = country;
    }

    @Override
    public boolean test(final Location location) {
        return location.getCountry().equals(country);
    }
}
