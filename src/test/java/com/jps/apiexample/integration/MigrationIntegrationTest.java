package com.jps.apiexample.integration;

import com.jps.apiexample.testutils.TestConfig;
import com.jps.apiexample.testutils.CsvGenerator;
import com.jps.apiexample.testutils.HttpTestUtils;
import com.jps.apiexample.testutils.TestServerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el endpoint de migración
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class MigrationIntegrationTest extends TestServerUtils {
    
    @Test
    void testMigrateEndpoint_ValidCsv_ShouldReturnOk() {
        // Arrange
        String csvContent = CsvGenerator.generateTestCsv(3);
        
        // Act
        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                getMigrateUrl(), "csv_file", "test.csv", csvContent);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody()); // El endpoint no devuelve body (null)
    }
    
    @Test
    void testMigrateEndpoint_EmptyFile_ShouldReturnBadRequest() {
        // Act
        try {
            HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "empty.csv", "");
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testMigrateEndpoint_InvalidFileExtension_ShouldReturnBadRequest() {
        // Arrange
        String csvContent = CsvGenerator.generateTestCsv(3);
        
        // Act
        try {
            HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "test.txt", csvContent);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testMigrateEndpoint_InvalidCsvContent_ShouldReturnBadRequest() {
        // Arrange
        String invalidCsv = "wrong,header,format\n1,1001,150.50";
        
        // Act
        try {
            HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "invalid.csv", invalidCsv);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 400 Bad Request
            assertEquals(400, e.getStatusCode().value());
        }
    }
    
    @Test
    void testMigrateEndpoint_NoFile_ShouldReturnBadRequest() {
        // Act - Request sin archivo
        try {
            HttpTestUtils.post(getMigrateUrl(), null);
            fail("Expected HttpClientErrorException");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Assert - Debe devolver 415 Unsupported Media Type o 400 Bad Request
            assertTrue(e.getStatusCode().value() == 400 || e.getStatusCode().value() == 415);
        }
    }
    
    @Test
    void testMigrateEndpoint_LargeCsv_ShouldReturnOk() {
        // Arrange
        String csvContent = CsvGenerator.generateTestCsv(1000);
        
        // Act
        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                getMigrateUrl(), "csv_file", "large_test.csv", csvContent);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void testMigrateEndpoint_DifferentDateFormats_ShouldReturnOk() {
        // Arrange
        String csvContent = CsvGenerator.generateCsvWithDifferentDateFormats(10);
        
        // Act
        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                getMigrateUrl(), "csv_file", "date_formats.csv", csvContent);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
