package com.jacoblucas.covid19tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.BeforeClass;

public class TestBase {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void setUpTestBase() {
        OBJECT_MAPPER.registerModule(new Jdk8Module());
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.registerModule(new GuavaModule());
    }
}
