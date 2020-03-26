package com.jacoblucas.covid19tracker.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperFactory {
    private static ObjectMapper OBJECT_MAPPER;

    public static ObjectMapper getInstance() {
        if (OBJECT_MAPPER == null) {
            OBJECT_MAPPER = new ObjectMapper();
            OBJECT_MAPPER.registerModule(new Jdk8Module());
            OBJECT_MAPPER.registerModule(new JavaTimeModule());
            OBJECT_MAPPER.registerModule(new GuavaModule());
        }

        return OBJECT_MAPPER;
    }
}
