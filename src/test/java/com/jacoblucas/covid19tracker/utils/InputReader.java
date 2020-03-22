package com.jacoblucas.covid19tracker.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputReader {
    private static final String INPUT_DIR = "src/test/resources/";

    public static Stream<String> read(final String filename) throws IOException {
        return readFile(INPUT_DIR, filename);
    }

    public static String readAll(final String filename) throws IOException {
        return readFile(INPUT_DIR, filename).collect(Collectors.joining());
    }

    static Stream<String> readFile(final String path, final String filename) throws IOException {
        return Files.readAllLines(Paths.get(path + filename), StandardCharsets.UTF_8).stream();
    }
}
