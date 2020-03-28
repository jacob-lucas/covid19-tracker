package com.jacoblucas.covid19tracker;

import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.jhu.Location;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String TSD_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public static void main(String[] args) throws IOException {
        final JohnsHopkinsCovid19Adapter adapter = new JohnsHopkinsCovid19Adapter(new HttpClient(), TSD_URL);
        final List<Location> data = adapter.getAllLocationData();
//        data.forEach(System.out::println);

        final Location australia = adapter.getLocationData("Australia").get();
        System.out.println(australia);

        australia.getDateCountData().forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
            System.out.println("---");
        });
    }
}
