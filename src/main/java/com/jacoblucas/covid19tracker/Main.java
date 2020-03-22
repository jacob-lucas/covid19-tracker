package com.jacoblucas.covid19tracker;

import com.jacoblucas.covid19tracker.adapter.Covid19Adapter;
import com.jacoblucas.covid19tracker.model.Location;
import com.jacoblucas.covid19tracker.model.Locations;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final Covid19Adapter covid19Adapter = new Covid19Adapter();
        final Locations locations = covid19Adapter.getAllLocations(false);
        System.out.println(locations);
        final Locations aus = covid19Adapter.getLocationsByCountry("AU", true);
        System.out.println(aus);
        final Location wa = covid19Adapter.getLocation(49);
        System.out.println(wa);
    }
}
