package com.jps.apiexample.integration;

import com.jps.apiexample.testutils.TestConfig;
import com.jps.apiexample.testutils.HttpTestUtils;
import com.jps.apiexample.testutils.TestServerUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el endpoint de health
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class HealthIntegrationTest extends TestServerUtils {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testHealthEndpoint() throws Exception {
        // Act
        ResponseEntity<String> response = HttpTestUtils.get(getHealthUrl());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertEquals("healthy", jsonResponse.get("status").asText());
        assertEquals("spring_boot_api_example", jsonResponse.get("service").asText());
        assertTrue(jsonResponse.has("timestamp"));
    }
    
    @Test
    void testHealthEndpointWithWrongMethod() {
        // Act - Intentar POST en lugar de GET
        try {
            HttpTestUtils.post(getHealthUrl(), null);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 405 Method Not Allowed
            assertEquals(405, e.getStatusCode().value());
        }
    }
}
