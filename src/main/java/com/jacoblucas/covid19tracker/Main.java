package com.jacoblucas.covid19tracker;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.adapters.Covid19Adapter;
import com.jacoblucas.covid19tracker.reports.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.reports.ReportGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final Covid19Adapter covid19Adapter = new Covid19Adapter();
        final ReportGenerator reportGenerator = new ReportGenerator(covid19Adapter);

        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(ImmutableMap.of(
//                "province", "Washington",
                "country_code", "US"));
        System.out.println(dailyConfirmedCasesDeltaReport);
    }
}
