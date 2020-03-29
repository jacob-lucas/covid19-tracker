package com.jacoblucas.covid19tracker.adapters;

import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.jhu.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Provides access to JHU COVID-19 data, found at https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data
public class JohnsHopkinsCovid19Adapter {
    public static final String NAME = "CSSE at Johns Hopkins University";
    public static final String DELIMITER = ",";
    public static final String DATE_FORMAT = "MM/dd/yy";

    private final HttpClient httpClient;
    private final String tsdUrl;

    public JohnsHopkinsCovid19Adapter(
            final HttpClient httpClient,
            final String tsdUrl
    ) {
        this.httpClient = httpClient;
        this.tsdUrl = tsdUrl;
    }

    public List<Location> getAllLocationData() throws IOException {
        final List<String> raw = downloadRawData();
        return Location.parse(raw);
    }

    public Optional<Location> getLocationData(final String country) throws IOException {
        final List<Location> all = getAllLocationData();

        final Map<String, List<Location>> dataByCountry = all.stream()
                .collect(Collectors.groupingBy(Location::getCountry));

        final List<Location> countryData = dataByCountry.get(country);
        if (countryData == null) {
            return Optional.empty();
        }

        return Optional.of(Location.aggregateByCountry(countryData));
    }

    private ArrayList<String> downloadRawData() throws IOException {
        final String raw = httpClient.get(tsdUrl);
        return new ArrayList<>(Arrays.asList(raw.split("\n")));
    }
}
