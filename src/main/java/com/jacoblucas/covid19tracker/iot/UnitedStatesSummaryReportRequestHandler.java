package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jacoblucas.covid19tracker.adapters.CovidTrackingProjectAdapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.iot.requests.UnitedStatesSummaryReportRequest;
import com.jacoblucas.covid19tracker.models.ctp.StateSummary;
import com.jacoblucas.covid19tracker.models.ctp.UnitedStatesSummary;
import com.jacoblucas.covid19tracker.reports.ctp.ReportGenerator;
import com.jacoblucas.covid19tracker.utils.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class UnitedStatesSummaryReportRequestHandler extends RequestHandler implements RequestStreamHandler {
    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        final Environment env = new Environment();
        final HttpClient httpClient = new HttpClient();
        final CovidTrackingProjectAdapter adapter = new CovidTrackingProjectAdapter(httpClient, env.get("DATA_LOCATION"), objectMapper);

        final UnitedStatesSummaryReportRequest request = objectMapper.readValue(input, new TypeReference<UnitedStatesSummaryReportRequest>() {});
        log(context, "Received %s", request);

        final ReportGenerator reportGenerator = new ReportGenerator(adapter);

        if (request.getFromDate().isPresent() && request.getToDate().isPresent()) {
            // historical report
            if (request.getState().isPresent()) {
                // state report
                final List<StateSummary> summaries = reportGenerator.generateHistoricStateSummary(request);
                objectMapper.writeValue(output, summaries);
            } else {
                // US report
                final List<UnitedStatesSummary> summaries = reportGenerator.generateHistoricUnitedStatesSummary(request);
                objectMapper.writeValue(output, summaries);
            }
        } else {
            // current report
            if (request.getState().isPresent()) {
                // state report
                final StateSummary summary = reportGenerator.generateStateSummary(request);
                objectMapper.writeValue(output, summary);
            } else {
                // US report
                final UnitedStatesSummary summary = reportGenerator.generateUnitedStatesSummary();
                objectMapper.writeValue(output, summary);
            }
        }

    }
}
