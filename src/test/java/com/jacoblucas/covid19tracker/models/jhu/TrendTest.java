package com.jacoblucas.covid19tracker.models.jhu;

import com.google.common.collect.ImmutableList;
import com.jacoblucas.covid19tracker.models.ImmutableTrend;
import com.jacoblucas.covid19tracker.models.LocationStatus;
import com.jacoblucas.covid19tracker.models.Trend;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TrendTest {

    @Test
    public void testStableCountry() {
        final String country = "myCountry";
        final List<Integer> windows = ImmutableList.of(0, 0, 0, 0, 0);
        final Trend trend = ImmutableTrend.builder()
                .location(country)
                .windows(windows)
                .windowSize(1)
                .build();

        assertThat(trend.getWindowCount(), is(5));
        assertThat(trend.getGradient(), is(0.0F));
        assertThat(trend.getLocationStatus(), is(LocationStatus.STABLE));
    }

    @Test
    public void testOutbreakingCountry() {
        final String country = "myCountry";
        final List<Integer> windows = ImmutableList.of(6352543, 563737, 45654, 986, 10);
        final Trend trend = ImmutableTrend.builder()
                .location(country)
                .windows(windows)
                .windowSize(1)
                .build();

        assertThat(trend.getWindowCount(), is(5));
        assertThat(trend.getGradient(), is(1270506.6F));
        assertThat(trend.getLocationStatus(), is(LocationStatus.OUTBREAKING));
    }

    @Test
    public void testFlatteningCountry() {
        final String country = "myCountry";
        final List<Integer> windows = ImmutableList.of(50, 48, 48, 47, 47);
        final Trend trend = ImmutableTrend.builder()
                .location(country)
                .windows(windows)
                .windowSize(1)
                .build();

        assertThat(trend.getWindowCount(), is(5));
        assertThat(trend.getGradient(), is(0.6F));
        assertThat(trend.getLocationStatus(), is(LocationStatus.FLATTENING));
    }

    @Test
    public void testRecoveringCountry() {
        final String country = "myCountry";
        final List<Integer> windows = ImmutableList.of(5, 48, 148, 3547, 11147);
        final Trend trend = ImmutableTrend.builder()
                .location(country)
                .windows(windows)
                .windowSize(1)
                .build();

        assertThat(trend.getWindowCount(), is(5));
        assertThat(trend.getGradient(), is(-2228.4F));
        assertThat(trend.getLocationStatus(), is(LocationStatus.RECOVERING));
    }
}
