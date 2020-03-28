package com.jacoblucas.covid19tracker.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnvironmentTest {
    @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void usesSystemEnvWhenNoProperties() {
        environmentVariables.set("foo", "bar");
        final Environment env = new Environment();
        assertThat(env.get("foo"), is("bar"));
    }

    @Test
    public void useProvidedPropertiesWhenAvailable() {
        final Properties properties = new Properties();
        properties.put("foo", "bar");
        final Environment env = new Environment(properties);
        assertThat(env.get("foo"), is("bar"));
    }
}
