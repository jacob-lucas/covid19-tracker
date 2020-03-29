package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.iot.requests.DailyConfirmedCasesDeltaReportRequest;
import com.jacoblucas.covid19tracker.models.DailyConfirmedCasesDeltaReport;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;
import com.jacoblucas.covid19tracker.utils.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

public class DailyConfirmedCasesDeltaReportRequestHandler extends RequestHandler implements RequestStreamHandler {
    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        final Instant start = Instant.now();
        final DailyConfirmedCasesDeltaReportRequest request = objectMapper.readValue(input, new TypeReference<DailyConfirmedCasesDeltaReportRequest>() {});
        log(context, "Received %s", request);

        final HttpClient httpClient = new HttpClient();
        final String tsdUrl = new Environment().get("TSD_URL");
        final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter = new JohnsHopkinsCovid19Adapter(httpClient, tsdUrl);
        final ReportGenerator reportGenerator = new ReportGenerator(johnsHopkinsCovid19Adapter);

        final DailyConfirmedCasesDeltaReport dailyConfirmedCasesDeltaReport = reportGenerator.generateDailyConfirmedCasesDeltaReport(request.getFilters());
        objectMapper.writeValue(output, dailyConfirmedCasesDeltaReport);

        final long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
        log(context, "Generated in %d ms : %s", duration, dailyConfirmedCasesDeltaReport);
    }
}
