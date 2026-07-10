package com.claude.framework.exceptions;

/**
 * Root unchecked exception for all framework-level failures (configuration,
 * driver bootstrap, page-object misuse, reporting). Keeping a single root
 * type lets callers catch broadly ("framework blew up") without swallowing
 * assertion or test-logic errors that TestNG needs to see.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
