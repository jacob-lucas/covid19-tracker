package com.jacoblucas.covid19tracker.http;

import com.google.common.collect.ImmutableMap;
import com.jacoblucas.covid19tracker.TestBase;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpClientTest extends TestBase {

    @Test
    public void testToQuerystring() {
        final Map<String, String> querystringMap = ImmutableMap.of(
                "a", "1",
                "b", "2",
                "c", "3");

        final String querystring = new HttpClient().toQuerystring(querystringMap);

        assertThat(querystring, is("?a=1&b=2&c=3"));
    }
}
