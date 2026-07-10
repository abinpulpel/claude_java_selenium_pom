package com.claude.framework.config;

import com.claude.framework.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration reader implemented as a thread-safe lazy
 * singleton (Singleton pattern, used deliberately since configuration is
 * process-wide and immutable once loaded).
 *
 * <p>Resolution order (highest priority first):
 * <ol>
 *     <li>JVM system property, e.g. {@code -Dbrowser=firefox}</li>
 *     <li>{@code config-<env>.properties} (env resolved from {@code -Denv}, default "qa")</li>
 *     <li>{@code config.properties} (framework defaults)</li>
 * </ol>
 */
public final class ConfigManager {

    private static volatile ConfigManager instance;

    private final Properties properties = new Properties();

    private ConfigManager() {
        loadPropertiesFile("config.properties");
        String env = System.getProperty("env", "qa");
        loadPropertiesFile("config-" + env + ".properties");
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadPropertiesFile(String fileName) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (stream == null) {
                // Environment overlay files are optional; the base file is not.
                if (fileName.equals("config.properties")) {
                    throw new FrameworkException("Required resource '" + fileName + "' was not found on the classpath");
                }
                return;
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new FrameworkException("Failed to load configuration file: " + fileName, e);
        }
    }

    public String get(String key) {
        String systemOverride = System.getProperty(key);
        if (systemOverride != null) {
            return systemOverride;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new FrameworkException("Missing required configuration key: " + key);
        }
        return value;
    }

    public String get(String key, String defaultValue) {
        String systemOverride = System.getProperty(key);
        if (systemOverride != null) {
            return systemOverride;
        }
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }
}
