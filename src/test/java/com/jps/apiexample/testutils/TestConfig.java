package com.jps.apiexample.testutils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuración para las pruebas
 */
@Component
public class TestConfig {
    
    @Value("${test.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${test.path-api:/api/v1}")
    private String pathApi;
    
    @Value("${test.timeout:30000}")
    private long timeout;
    
    @Value("${test.max-retries:3}")
    private int maxRetries;
    
    @Value("${test.retry-delay:1000}")
    private long retryDelay;
    
    @Value("${test.data-dir:src/test/resources/testdata}")
    private String testDataDir;
    
    // Getters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public String getPathApi() {
        return pathApi;
    }
    
    public String getHostApi() {
        return baseUrl + pathApi;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public long getRetryDelay() {
        return retryDelay;
    }
    
    public String getTestDataDir() {
        return testDataDir;
    }
    
    // Static methods for easy access
    public static String getBaseUrlStatic() {
        return "http://localhost:8080";
    }
    
    public static String getPathApiStatic() {
        return "/api/v1";
    }
    
    public static String getHostApiStatic() {
        return getBaseUrlStatic() + getPathApiStatic();
    }
    
    public static long getTimeoutStatic() {
        return 30000;
    }
}
