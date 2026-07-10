package com.claude.framework.utils;

import com.claude.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializes JSON test-data fixtures (under {@code src/test/resources})
 * into POJOs, so data-driven tests never hand-parse JSON inline. Repository
 * pattern applied narrowly here: this is the single access point for
 * on-disk test data.
 */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
    }

    public static <T> T read(String classpathResource, Class<T> type) {
        try (InputStream stream = JsonDataReader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (stream == null) {
                throw new FrameworkException("Test data file not found on classpath: " + classpathResource);
            }
            return MAPPER.readValue(stream, type);
        } catch (IOException e) {
            throw new FrameworkException("Failed to parse test data file: " + classpathResource, e);
        }
    }
}
