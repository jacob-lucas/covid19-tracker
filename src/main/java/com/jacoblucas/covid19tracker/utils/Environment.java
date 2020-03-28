package com.jacoblucas.covid19tracker.utils;

import java.util.Properties;

public class Environment {
    private final Properties properties;

    public Environment() {
        this.properties = null;
    }

    public Environment(final Properties properties) {
        this.properties = properties;
    }

    public String get(final String name) {
        if (properties != null) {
            return properties.getProperty(name);
        } else {
            return System.getenv(name);
        }
    }
}
