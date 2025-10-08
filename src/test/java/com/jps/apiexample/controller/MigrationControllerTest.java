package com.jps.apiexample.controller;

import com.jps.apiexample.model.MigrationReport;
import com.jps.apiexample.service.MigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para MigrationController
 */
@ExtendWith(MockitoExtension.class)
class MigrationControllerTest {
    
    @Mock
    private MigrationService migrationService;
    
    @InjectMocks
    private MigrationController migrationController;
    
    private MockMultipartFile validCsvFile;
    private MockMultipartFile invalidCsvFile;
    private MockMultipartFile emptyFile;
    
    @BeforeEach
    void setUp() {
        // Archivo CSV válido
        String csvContent = "id,user_id,amount,datetime\n" +
                "1,1001,150.50,2024-01-15 10:30:00\n" +
                "2,1001,-75.25,2024-01-15 14:45:00";
        
        validCsvFile = new MockMultipartFile(
                "csv_file",
                "test_transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );
        
        // Archivo CSV inválido (sin extensión .csv)
        invalidCsvFile = new MockMultipartFile(
                "csv_file",
                "test_transactions.txt",
                "text/plain",
                csvContent.getBytes()
        );
        
        // Archivo vacío
        emptyFile = new MockMultipartFile(
                "csv_file",
                "empty.csv",
                "text/csv",
                new byte[0]
        );
    }
    
    @Test
    void testMigrateCsv_ValidFile_ShouldReturnOk() throws Exception {
        // Arrange
        MigrationReport mockReport = new MigrationReport();
        mockReport.setTotalRecords(2);
        mockReport.setSuccessRecords(2);
        mockReport.setErrorRecords(0);
        
        when(migrationService.processCsv(validCsvFile)).thenReturn(mockReport);
        
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(validCsvFile);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(migrationService).processCsv(validCsvFile);
    }
    
    @Test
    void testMigrateCsv_EmptyFile_ShouldReturnBadRequest() throws Exception {
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(emptyFile);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(migrationService, never()).processCsv(any(MultipartFile.class));
    }
    
    @Test
    void testMigrateCsv_InvalidFileExtension_ShouldReturnBadRequest() throws Exception {
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(invalidCsvFile);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(migrationService, never()).processCsv(any(MultipartFile.class));
    }
    
    @Test
    void testMigrateCsv_InvalidCsvContent_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(migrationService.processCsv(validCsvFile))
                .thenThrow(new IllegalArgumentException("Invalid CSV header"));
        
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(validCsvFile);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(migrationService).processCsv(validCsvFile);
    }
    
    @Test
    void testMigrateCsv_ServiceException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(migrationService.processCsv(validCsvFile))
                .thenThrow(new RuntimeException("Internal error"));
        
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(validCsvFile);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        verify(migrationService).processCsv(validCsvFile);
    }
    
    @Test
    void testMigrateCsv_NullFilename_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MockMultipartFile fileWithNullName = new MockMultipartFile(
                "csv_file",
                null,
                "text/csv",
                "id,user_id,amount,datetime\n1,1001,150.50,2024-01-15 10:30:00".getBytes()
        );
        
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(fileWithNullName);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(migrationService, never()).processCsv(any(MultipartFile.class));
    }
    
    @Test
    void testMigrateCsv_WrongContentType_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MockMultipartFile fileWithWrongContentType = new MockMultipartFile(
                "csv_file",
                "test.csv",
                "application/json",
                "id,user_id,amount,datetime\n1,1001,150.50,2024-01-15 10:30:00".getBytes()
        );
        
        // Act
        ResponseEntity<Void> response = migrationController.migrateCsv(fileWithWrongContentType);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        verify(migrationService, never()).processCsv(any(MultipartFile.class));
    }
}
