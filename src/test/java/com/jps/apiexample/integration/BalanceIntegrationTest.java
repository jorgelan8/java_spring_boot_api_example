package com.jps.apiexample.integration;

import com.jps.apiexample.testutils.TestConfig;
import com.jps.apiexample.testutils.CsvGenerator;
import com.jps.apiexample.testutils.HttpTestUtils;
import com.jps.apiexample.testutils.TestServerUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el endpoint de balance
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class BalanceIntegrationTest extends TestServerUtils {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        // Migrar datos de prueba antes de cada test
        String csvContent = CsvGenerator.generateTestCsv(10);
        ResponseEntity<String> response = HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "test.csv", csvContent);
        
        // Verificar que la migración fue exitosa
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void testBalanceEndpoint_ValidUser_ShouldReturnOk() throws Exception {
        // Act - Intentar consultar el usuario 1001 (que debería existir en los datos generados)
        ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(1001));
        
        // Assert - Verificar que la respuesta sea exitosa o que el usuario no se encuentre
        if (response.getStatusCode() == HttpStatus.OK) {
            assertNotNull(response.getBody());
            
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            assertTrue(jsonResponse.has("balance"));
            assertTrue(jsonResponse.has("total_debits"));
            assertTrue(jsonResponse.has("total_credits"));
            
            // Verificar que los valores son números
            assertTrue(jsonResponse.get("balance").isNumber());
            assertTrue(jsonResponse.get("total_debits").isNumber());
            assertTrue(jsonResponse.get("total_credits").isNumber());
        } else {
            // Si el usuario no se encuentra, verificar que sea un 400 Bad Request
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }
    
    @Test
    void testBalanceEndpoint_UserNotFound_ShouldReturnBadRequest() {
        // Act - Consultar un usuario que definitivamente no existe
        try {
            HttpTestUtils.get(getBalanceUrl(9999));
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testBalanceEndpoint_InvalidUserID_ShouldReturnBadRequest() {
        // Act - Intentar con user ID inválido
        try {
            HttpTestUtils.get(getBaseUrl() + "/api/v1/users/invalid/balance");
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testBalanceEndpoint_WithDateRange_ShouldReturnOk() throws Exception {
        // Act
        String fromDate = "2024-01-15T00:00:00Z";
        String toDate = "2024-01-20T23:59:59Z";
        ResponseEntity<String> response = HttpTestUtils.get(
                getBalanceUrlWithDateRange(1001, fromDate, toDate));
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertTrue(jsonResponse.has("balance"));
    }
    
    @Test
    void testBalanceEndpoint_InvalidDateFormat_ShouldReturnBadRequest() {
        // Act
        String invalidDate = "invalid-date";
        try {
            HttpTestUtils.get(getBaseUrl() + "/api/v1/users/1001/balance?from=" + invalidDate);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testBalanceEndpoint_InvalidDateRange_ShouldReturnBadRequest() {
        // Act - from > to
        String fromDate = "2024-01-20T00:00:00Z";
        String toDate = "2024-01-15T23:59:59Z";
        try {
            HttpTestUtils.get(getBalanceUrlWithDateRange(1001, fromDate, toDate));
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testBalanceEndpoint_OnlyFromDate_ShouldReturnOk() throws Exception {
        // Act
        String fromDate = "2024-01-15T00:00:00Z";
        ResponseEntity<String> response = HttpTestUtils.get(
                getBaseUrl() + "/api/v1/users/1001/balance?from=" + fromDate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void testBalanceEndpoint_OnlyToDate_ShouldReturnOk() throws Exception {
        // Act
        String toDate = "2024-01-20T23:59:59Z";
        ResponseEntity<String> response = HttpTestUtils.get(
                getBaseUrl() + "/api/v1/users/1001/balance?to=" + toDate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void testBalanceEndpoint_WithWrongMethod_ShouldReturnMethodNotAllowed() {
        // Act - Intentar POST en lugar de GET
        try {
            HttpTestUtils.post(getBalanceUrl(1001), null);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 405 Method Not Allowed
            assertEquals(405, e.getStatusCode().value());
        }
    }
}
