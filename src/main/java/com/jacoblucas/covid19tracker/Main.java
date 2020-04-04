package com.jacoblucas.covid19tracker;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;

import java.io.IOException;

public class Main {

    private static final String TSD_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public static void main(String[] args) throws IOException {
        final JohnsHopkinsCovid19Adapter adapter = new JohnsHopkinsCovid19Adapter(new HttpClient(), TSD_URL);
        final ReportGenerator reportGenerator = new ReportGenerator(adapter);
        final DailyNewCasesReport australia = reportGenerator.generateDailyConfirmedCasesDeltaReport(
                ImmutableMap.of("fromDate", "3/15/20", "toDate", "3/19/20", "country", "Australia"));
        System.out.println(australia);

        final DailyNewCasesReport italy = reportGenerator.generateDailyConfirmedCasesDeltaReport(
                ImmutableMap.of("country", "Italy"));
        System.out.println(italy);

        final DailyNewCasesReport usa = reportGenerator.generateDailyConfirmedCasesDeltaReport(
                ImmutableMap.of("country", "US"));
        System.out.println(usa);

        final DailyNewCasesReport china = reportGenerator.generateDailyConfirmedCasesDeltaReport(
                ImmutableMap.of("country", "China"));
        System.out.println(china);
    }
}
