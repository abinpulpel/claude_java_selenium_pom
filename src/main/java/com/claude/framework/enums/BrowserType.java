package com.claude.framework.enums;

/**
 * Supported browser targets for local and remote (Grid/cloud) execution.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    /**
     * Case-insensitive lookup with a clear error for unsupported values,
     * rather than letting {@link IllegalArgumentException} bubble up with
     * Java's default enum message.
     *
     * @param value browser name, e.g. "chrome" or "Chrome"
     * @return the matching {@link BrowserType}
     */
    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Browser type must not be null or blank");
        }
        try {
            return BrowserType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Unsupported browser '" + value + "'. Supported values: chrome, firefox, edge, safari", ex);
        }
    }
}
