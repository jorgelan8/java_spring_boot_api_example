package com.jps.apiexample.testutils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Utilidades para configurar servidor de pruebas
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class TestServerUtils {
    
    @LocalServerPort
    protected int port;
    
    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    protected String getApiUrl() {
        return getBaseUrl() + TestConfig.getPathApiStatic();
    }
    
    protected String getHealthUrl() {
        return getApiUrl() + "/health";
    }
    
    protected String getMigrateUrl() {
        return getApiUrl() + "/migrate";
    }
    
    protected String getBalanceUrl(int userId) {
        return getApiUrl() + "/users/" + userId + "/balance";
    }
    
    protected String getBalanceUrlWithDateRange(int userId, String fromDate, String toDate) {
        return getApiUrl() + "/users/" + userId + "/balance?from=" + fromDate + "&to=" + toDate;
    }
}
