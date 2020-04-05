package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.jacoblucas.covid19tracker.adapters.JohnsHopkinsCovid19Adapter;
import com.jacoblucas.covid19tracker.http.HttpClient;
import com.jacoblucas.covid19tracker.models.jhu.Location;
import com.jacoblucas.covid19tracker.utils.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class WorldDataRequestHandler extends RequestHandler implements RequestStreamHandler {
    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        final HttpClient httpClient = new HttpClient();
        final String tsdUrl = new Environment().get("TSD_URL");
        final JohnsHopkinsCovid19Adapter johnsHopkinsCovid19Adapter = new JohnsHopkinsCovid19Adapter(httpClient, tsdUrl);
        final List<Location> worldData = johnsHopkinsCovid19Adapter.getAllLocationData();
        objectMapper.writeValue(output, worldData);
    }
}
