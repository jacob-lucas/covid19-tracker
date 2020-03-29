package com.jacoblucas.covid19tracker;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;

import java.io.IOException;

public class Main {

    private static final String TSD_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public static void main(String[] args) throws IOException {
        final JohnsHopkinsCovid19Adapter adapter = new JohnsHopkinsCovid19Adapter(new HttpClient(), TSD_URL);
        final ReportGenerator reportGenerator = new ReportGenerator(adapter);
        final DailyConfirmedCasesDeltaReport report = reportGenerator.generateDailyConfirmedCasesDeltaReport(
                ImmutableMap.of("fromDate", "3/15/20", "toDate", "3/19/20", "country", "US"));
        System.out.println(report);
    }
}
