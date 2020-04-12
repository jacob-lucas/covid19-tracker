package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.iot.requests.DailyNewCasesReportRequest;
import com.jacoblucas.covid19tracker.models.DailyNewCasesReport;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;
import com.jacoblucas.covid19tracker.utils.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

public class DailyNewCasesReportRequestHandler extends RequestHandler implements RequestStreamHandler {
    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        final Instant start = Instant.now();
        final DailyNewCasesReportRequest request = objectMapper.readValue(input, new TypeReference<DailyNewCasesReportRequest>() {});
        log(context, "Received %s", request);

        final HttpClient httpClient = new HttpClient();
        final String dataLocation = new Environment().get("DATA_LOCATION");
        final String tsdFile = new Environment().get("TSD_FILE");
        final String deathsFile = new Environment().get("DEATHS_FILE");
        final String recoveriesFile = new Environment().get("RECOVERIES_FILE");
        final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter = new JohnsHopkinsCovid19Adapter(httpClient, dataLocation, tsdFile, deathsFile, recoveriesFile);
        final ReportGenerator reportGenerator = new ReportGenerator(johnsHopkinsCovid19Adapter);

        final LocationDataType locationDataType = LocationDataType.of(request.getLocationDataType());
        final DailyNewCasesReport dailyNewCasesReport = reportGenerator.generateDailyNewCasesReport(request.getFilters(), locationDataType);
        objectMapper.writeValue(output, dailyNewCasesReport);

        final long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
        log(context, "Generated in %d ms : %s", duration, dailyNewCasesReport);
    }
}
