package com.jps.apiexample.service;

import com.jps.apiexample.model.MigrationReport;
import com.jps.apiexample.model.UserTransaction;
import com.jps.apiexample.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para MigrationService
 */
@ExtendWith(MockitoExtension.class)
class MigrationServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private ReportService reportService;
    
    @InjectMocks
    private MigrationService migrationService;
    
    private MockMultipartFile validCsvFile;
    private MockMultipartFile invalidCsvFile;
    
    @BeforeEach
    void setUp() {
        // Archivo CSV válido
        String csvContent = "id,user_id,amount,datetime\n" +
                "1,1001,150.50,2024-01-15 10:30:00\n" +
                "2,1001,-75.25,2024-01-15 14:45:00\n" +
                "3,1002,200.00,2024-01-16 09:15:00";
        
        validCsvFile = new MockMultipartFile(
                "csv_file",
                "test_transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );
        
        // Archivo CSV inválido
        String invalidCsvContent = "wrong,header,format\n" +
                "1,1001,150.50";
        
        invalidCsvFile = new MockMultipartFile(
                "csv_file",
                "invalid_transactions.csv",
                "text/csv",
                invalidCsvContent.getBytes()
        );
    }
    
    @Test
    void testProcessCsv_ValidFile_ShouldProcessSuccessfully() throws IOException, InterruptedException {
        // Arrange
        UserTransaction savedTransaction = new UserTransaction(1, 1001, 150.50, 
                LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        when(transactionRepository.save(any(UserTransaction.class))).thenReturn(savedTransaction);
        
        // Act
        MigrationReport report = migrationService.processCsv(validCsvFile);
        
        // Assert
        assertNotNull(report);
        assertEquals(3, report.getTotalRecords());
        assertEquals(3, report.getSuccessRecords());
        assertEquals(0, report.getErrorRecords());
        assertEquals("test_transactions.csv", report.getFilename());
        assertTrue(report.getUsersAffected() > 0);
        
        verify(transactionRepository, times(3)).save(any(UserTransaction.class));
        
        // Esperar un poco para que se complete la operación asíncrona
        Thread.sleep(100);
        verify(reportService).sendMigrationReport(any(MigrationReport.class));
    }
    
    @Test
    void testProcessCsv_InvalidHeader_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            migrationService.processCsv(invalidCsvFile);
        });
        
        verify(transactionRepository, never()).save(any(UserTransaction.class));
        verify(reportService, never()).sendMigrationReport(any(MigrationReport.class));
    }
    
    @Test
    void testProcessCsv_EmptyFile_ShouldThrowException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "csv_file",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            migrationService.processCsv(emptyFile);
        });
    }
    
    @Test
    void testProcessCsv_WithErrors_ShouldProcessPartialSuccess() throws IOException {
        // Arrange
        String csvWithErrors = "id,user_id,amount,datetime\n" +
                "1,1001,150.50,2024-01-15 10:30:00\n" +
                "2,invalid_user,invalid_amount,invalid_date\n" +
                "3,1002,200.00,2024-01-16 09:15:00";
        
        MockMultipartFile csvFileWithErrors = new MockMultipartFile(
                "csv_file",
                "transactions_with_errors.csv",
                "text/csv",
                csvWithErrors.getBytes()
        );
        
        UserTransaction savedTransaction = new UserTransaction(1, 1001, 150.50, 
                LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        when(transactionRepository.save(any(UserTransaction.class))).thenReturn(savedTransaction);
        
        // Act
        MigrationReport report = migrationService.processCsv(csvFileWithErrors);
        
        // Assert
        assertNotNull(report);
        assertEquals(3, report.getTotalRecords());
        assertEquals(2, report.getSuccessRecords());
        assertEquals(1, report.getErrorRecords());
        assertNotNull(report.getErrors());
        assertFalse(report.getErrors().isEmpty());
        
        verify(transactionRepository, times(2)).save(any(UserTransaction.class));
    }
    
    @Test
    void testProcessCsv_DifferentDateFormats_ShouldProcessSuccessfully() throws IOException {
        // Arrange
        String csvWithDifferentDates = "id,user_id,amount,datetime\n" +
                "1,1001,150.50,2024-01-15 10:30:00\n" +
                "2,1001,200.00,2024-01-16T09:15:00\n" +
                "3,1001,300.00,2024-01-17";
        
        MockMultipartFile csvFileWithDifferentDates = new MockMultipartFile(
                "csv_file",
                "transactions_different_dates.csv",
                "text/csv",
                csvWithDifferentDates.getBytes()
        );
        
        UserTransaction savedTransaction = new UserTransaction(1, 1001, 150.50, 
                LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        when(transactionRepository.save(any(UserTransaction.class))).thenReturn(savedTransaction);
        
        // Act
        MigrationReport report = migrationService.processCsv(csvFileWithDifferentDates);
        
        // Assert
        assertNotNull(report);
        assertEquals(3, report.getTotalRecords());
        assertEquals(3, report.getSuccessRecords());
        assertEquals(0, report.getErrorRecords());
        
        verify(transactionRepository, times(3)).save(any(UserTransaction.class));
    }
}
