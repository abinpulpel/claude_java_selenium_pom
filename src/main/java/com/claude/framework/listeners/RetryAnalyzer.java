package com.claude.framework.listeners;

import com.claude.framework.config.ConfigManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to a configurable number of times before letting
 * the failure stand. Attach via {@code @Test(retryAnalyzer = RetryAnalyzer.class)}
 * or wire globally through an {@code IAnnotationTransformer} if every test
 * should be retried.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private final int maxRetryCount = ConfigManager.getInstance().getInt("retry.max.count", 2);

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}
