package com.jacoblucas.covid19tracker.reports.ctp;

import com.jacoblucas.covid19tracker.adapters.CovidTrackingProjectAdapter;
import com.jacoblucas.covid19tracker.iot.requests.UnitedStatesSummaryReportRequest;
import com.jacoblucas.covid19tracker.models.ctp.StateSummary;
import com.jacoblucas.covid19tracker.models.ctp.UnitedStatesSummary;

import java.io.IOException;
import java.util.List;

public class ReportGenerator {
    private final CovidTrackingProjectAdapter adapter;

    public ReportGenerator(final CovidTrackingProjectAdapter adapter) {
        this.adapter = adapter;
    }

    public UnitedStatesSummary generateUnitedStatesSummary() throws IOException {
        return adapter.getCurrentUnitedStatesSummary();
    }

    public List<UnitedStatesSummary> generateHistoricUnitedStatesSummary(final UnitedStatesSummaryReportRequest request) throws IOException {
        if (request.getFromDate().isPresent() && request.getToDate().isPresent()) {
            return adapter.getHistoricUnitedStatesSummary(
                    Integer.parseInt(request.getFromDate().get()),
                    Integer.parseInt(request.getToDate().get()));
        } else {
            return adapter.getHistoricUnitedStatesSummary();
        }
    }

    public List<StateSummary> generateHistoricStateSummary(final UnitedStatesSummaryReportRequest request) throws IOException {
        if (request.getFromDate().isPresent() && request.getToDate().isPresent() && request.getState().isPresent()) {
            return adapter.getHistoricValuesForState(
                    request.getState().get(),
                    Integer.parseInt(request.getFromDate().get()),
                    Integer.parseInt(request.getToDate().get()));
        } else {
            throw new IllegalArgumentException("Invalid historic state report request");
        }
    }

    public StateSummary generateStateSummary(final UnitedStatesSummaryReportRequest request) throws IOException {
        if (request.getState().isPresent()) {
            return adapter.getCurrentValuesForState(request.getState().get());
        } else {
            throw new IllegalArgumentException("Invalid state report request");
        }
    }
}
