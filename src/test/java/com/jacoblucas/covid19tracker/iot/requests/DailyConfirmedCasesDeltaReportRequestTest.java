package com.jacoblucas.covid19tracker.iot.requests;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DailyConfirmedCasesDeltaReportRequestTest {

    @Test(expected = IllegalArgumentException.class)
    public void fromWithoutToThrowsException() {
        ImmutableDailyConfirmedCasesDeltaReportRequest.builder()
                .filters(ImmutableMap.of("fromDate", "3/22/20", "country", "US"))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void toWithoutFromThrowsException() {
        ImmutableDailyConfirmedCasesDeltaReportRequest.builder()
                .filters(ImmutableMap.of("toDate", "3/21/20", "country", "US"))
                .build();
    }

    @Test
    public void emptyFiltersAllowed() {
        final DailyConfirmedCasesDeltaReportRequest request = ImmutableDailyConfirmedCasesDeltaReportRequest.builder()
                .filters(ImmutableMap.of())
                .build();

        assertThat(request, is(notNullValue()));
    }

}
