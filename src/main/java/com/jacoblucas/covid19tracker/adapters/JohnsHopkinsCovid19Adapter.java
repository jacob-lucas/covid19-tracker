package com.jacoblucas.covid19tracker.adapters;

import com.google.common.collect.ImmutableList;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Provides access to JHU COVID-19 data, found at https://github.com/CSSEGISandData/COVID-19/tree/master/csse_covid_19_data
public class JohnsHopkinsCovid19Adapter {
    public static final String NAME = "CSSE at Johns Hopkins University";
    public static final String DELIMITER = ",";
    public static final String DATE_FORMAT = "MM/dd/yy";

    private final HttpClient httpClient;
    private final String dataLocation;
    private final String tsdFile;
    private final String deathsFile;
    private final String recoveriesFile;

    public JohnsHopkinsCovid19Adapter(
            final HttpClient httpClient,
            final String dataLocation,
            final String tsdFile,
            final String deathsFile,
            final String recoveriesFile
    ) {
        this.httpClient = httpClient;
        this.dataLocation = dataLocation;
        this.tsdFile = tsdFile;
        this.deathsFile = deathsFile;
        this.recoveriesFile = recoveriesFile;
    }

    public List<Location> getAllLocationData(final LocationDataType locationDataType) throws IOException {
        if (locationDataType == LocationDataType.CONFIRMED_CASES) {
            return Location.parse(downloadRawTsdData(), locationDataType);
        } else if (locationDataType == LocationDataType.DEATHS) {
            return Location.parse(downloadRawDeathsData(), locationDataType);
        } else if (locationDataType == LocationDataType.RECOVERIES) {
            return Location.parse(downloadRawRecoveriesData(), locationDataType);
        } else {
            return ImmutableList.of();
        }
    }

    public List<Location> getUSLocationData() throws IOException {
        return Location.parseDetailed(download("time_series_covid19_confirmed_US.csv"), LocationDataType.CONFIRMED_CASES);
    }

    private ArrayList<String> downloadRawRecoveriesData() throws IOException {
        final String raw = httpClient.get(dataLocation + recoveriesFile);
        return new ArrayList<>(Arrays.asList(raw.split("\n")));
    }

    private ArrayList<String> downloadRawTsdData() throws IOException {
        final String raw = httpClient.get(dataLocation + tsdFile);
        return new ArrayList<>(Arrays.asList(raw.split("\n")));
    }

    private ArrayList<String> downloadRawDeathsData() throws IOException {
        final String raw = httpClient.get(dataLocation + deathsFile);
        return new ArrayList<>(Arrays.asList(raw.split("\n")));
    }

    // TODO: clean up above to all use this function instead
    private ArrayList<String> download(final String file) throws IOException {
        final String raw = httpClient.get(dataLocation + file);
        return new ArrayList<>(Arrays.asList(raw.split("\n")));
    }
}
