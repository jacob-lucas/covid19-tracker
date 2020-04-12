package com.jacoblucas.covid19tracker;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.WorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;

import java.io.IOException;

public class Main {

    private static final String DATA_LOCATION = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/";
    private static final String TSD_FILE = "time_series_covid19_confirmed_global.csv";
    private static final String DEATHS_FILE = "time_series_covid19_deaths_global.csv";
    private static final String RECOVERIES_FILE = "time_series_covid19_recovered_global.csv";

    public static void main(String[] args) throws IOException {
        final JohnsHopkinsCovid19Adapter adapter = new JohnsHopkinsCovid19Adapter(new HttpClient(), DATA_LOCATION, TSD_FILE, DEATHS_FILE, RECOVERIES_FILE);
        final ReportGenerator reportGenerator = new ReportGenerator(adapter);
        final DailyNewCasesReport australia = reportGenerator.generateDailyNewCasesReport(
                ImmutableMap.of("fromDate", "3/15/20", "toDate", "3/19/20", "country", "Australia"), LocationDataType.CONFIRMED_CASES);
        System.out.println(australia);

        final DailyNewCasesReport italy = reportGenerator.generateDailyNewCasesReport(
                ImmutableMap.of("country", "Italy"), LocationDataType.CONFIRMED_CASES);
        System.out.println(italy);

        final DailyNewCasesReport usa = reportGenerator.generateDailyNewCasesReport(
                ImmutableMap.of("country", "US"), LocationDataType.CONFIRMED_CASES);
        System.out.println(usa);

        final DailyNewCasesReport china = reportGenerator.generateDailyNewCasesReport(
                ImmutableMap.of("country", "China"), LocationDataType.CONFIRMED_CASES);
        System.out.println(china);

        final WorldDataSummaryReport casesReport = reportGenerator.generateWorldDataSummary(LocationDataType.CONFIRMED_CASES);
        casesReport.getLocationSummaries().forEach(System.out::println);
        System.out.println(casesReport.getReportGeneratedAt());
        System.out.println(casesReport.getUpdatedDate());

        final WorldDataSummaryReport deathsReport = reportGenerator.generateWorldDataSummary(LocationDataType.DEATHS);
        deathsReport.getLocationSummaries().forEach(System.out::println);
        System.out.println(deathsReport.getReportGeneratedAt());
        System.out.println(deathsReport.getUpdatedDate());

        final DailyNewCasesReport recoveriesReport = reportGenerator.generateDailyNewCasesReport(ImmutableMap.of(), LocationDataType.RECOVERIES);
        recoveriesReport.getDailyNewCases().forEach(System.out::println);
    }
}
