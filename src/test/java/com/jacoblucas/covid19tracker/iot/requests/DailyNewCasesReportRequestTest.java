package com.jacoblucas.covid19tracker.iot.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import com.jacoblucas.covid19tracker.utils.InputReader;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DailyNewCasesReportRequestTest extends TestBase {

    @Test(expected = IllegalArgumentException.class)
    public void fromWithoutToThrowsException() {
        ImmutableDailyNewCasesReportRequest.builder()
                .filters(ImmutableMap.of("fromDate", "3/22/20", "country", "US"))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void toWithoutFromThrowsException() {
        ImmutableDailyNewCasesReportRequest.builder()
                .filters(ImmutableMap.of("toDate", "3/21/20", "country", "US"))
                .build();
    }

    @Test
    public void emptyFiltersAllowed() {
        final DailyNewCasesReportRequest request = ImmutableDailyNewCasesReportRequest.builder()
                .filters(ImmutableMap.of())
                .build();

        assertThat(request, is(notNullValue()));
    }

    @Test
    public void ignoresUnknownProperties() throws IOException {
        final String json = InputReader.readAll("request.json", "\n");
        final DailyNewCasesReportRequest request = OBJECT_MAPPER.readValue(json, new TypeReference<DailyNewCasesReportRequest>(){});

        final DailyNewCasesReportRequest expected = ImmutableDailyNewCasesReportRequest.builder()
                .filters(ImmutableMap.of(
                        "fromDate", "3/1/20",
                        "toDate", "3/31/20",
                        "country", "Australia",
                        "state", "Western Australia"))
                .build();

        assertThat(request, is(expected));
    }
}
