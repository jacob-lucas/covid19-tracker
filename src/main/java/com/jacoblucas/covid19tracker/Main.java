package com.jacoblucas.covid19tracker;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.adapters.CoronaVirusTrackerApiAdapter;
import com.jacoblucas.covid19tracker.reports.coronavirustrackerapi.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.reports.coronavirustrackerapi.ReportGenerator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final CoronaVirusTrackerApiAdapter coronaVirusTrackerApiAdapter = new CoronaVirusTrackerApiAdapter();
        final ReportGenerator reportGenerator = new ReportGenerator(coronaVirusTrackerApiAdapter);

        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(ImmutableMap.of(
//                "province", "Washington",
                "country_code", "US"));
        System.out.println(dailyConfirmedCasesDeltaReport);
    }
}
