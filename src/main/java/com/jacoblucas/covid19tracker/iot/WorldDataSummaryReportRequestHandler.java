package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.WorldDataSummaryReport;
import com.jacoblucas.covid19tracker.models.jhu.LocationDataType;
import com.jacoblucas.covid19tracker.reports.jhu.ReportGenerator;
import com.jacoblucas.covid19tracker.utils.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WorldDataSummaryReportRequestHandler extends RequestHandler implements RequestStreamHandler {
    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        final HttpClient httpClient = new HttpClient();
        final String dataLocation = new Environment().get("DATA_LOCATION");
        final String tsdFile = new Environment().get("TSD_FILE");
        final String deathsFile = new Environment().get("DEATHS_FILE");
        final String recoveriesFile = new Environment().get("RECOVERIES_FILE");
        final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter = new JohnsHopkinsCovid19Adapter(httpClient, dataLocation, tsdFile, deathsFile, recoveriesFile);

        final ReportGenerator reportGenerator = new ReportGenerator(johnsHopkinsCovid19Adapter);
        final WorldDataSummaryReport report = reportGenerator.generateWorldDataSummary(LocationDataType.CONFIRMED_CASES);
        objectMapper.writeValue(output, report);
    }
}
