package com.jps.apiexample.service;

import com.jps.apiexample.model.MigrationReport;
import com.jps.apiexample.model.UserTransaction;
import com.jps.apiexample.repository.TransactionRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para manejar la migración de datos desde archivos CSV
 */
@Service
public class MigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    
    private final TransactionRepository transactionRepository;
    private final ReportService reportService;
    
    @Autowired
    public MigrationService(TransactionRepository transactionRepository, ReportService reportService) {
        this.transactionRepository = transactionRepository;
        this.reportService = reportService;
    }
    
    /**
     * Procesa un archivo CSV y migra las transacciones
     */
    public MigrationReport processCsv(MultipartFile file) throws IOException {
        LocalDateTime startTime = LocalDateTime.now();
        
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty");
            }
            
            // Validar header
            String[] header = records.get(0);
            validateHeader(header);
            
            // Procesar registros (saltar header)
            List<String[]> dataRecords = records.subList(1, records.size());
            MigrationStats stats = processRecords(dataRecords);
            
            // Calcular tiempo de procesamiento
            Duration processingTime = Duration.between(startTime, LocalDateTime.now());
            
            // Generar reporte
            MigrationReport report = generateMigrationReport(stats, file.getOriginalFilename(), 
                    file.getSize(), processingTime);
            
            // Enviar reporte de forma asíncrona
            CompletableFuture.runAsync(() -> reportService.sendMigrationReport(report));
            
            return report;
        } catch (CsvException e) {
            throw new IOException("Error reading CSV file", e);
        }
    }
    
    /**
     * Valida que el header del CSV sea correcto
     */
    private void validateHeader(String[] header) {
        String[] expectedHeader = {"id", "user_id", "amount", "datetime"};
        
        if (header.length != expectedHeader.length) {
            throw new IllegalArgumentException(
                    String.format("Invalid CSV header length. Expected: %d, Got: %d", 
                            expectedHeader.length, header.length));
        }
        
        for (int i = 0; i < expectedHeader.length; i++) {
            if (!expectedHeader[i].equals(header[i])) {
                throw new IllegalArgumentException(
                        String.format("Invalid CSV header. Expected: %s, Got: %s", 
                                Arrays.toString(expectedHeader), Arrays.toString(header)));
            }
        }
    }
    
    /**
     * Procesa los registros del CSV
     */
    private MigrationStats processRecords(List<String[]> records) {
        MigrationStats stats = new MigrationStats();
        stats.totalRecords = records.size();
        
        for (int i = 0; i < records.size(); i++) {
            String[] record = records.get(i);
            int lineNumber = i + 2; // +2 porque empezamos desde línea 2
            
            try {
                UserTransaction transaction = parseTransaction(record, lineNumber);
                UserTransaction savedTransaction = transactionRepository.save(transaction);
                stats.updateSuccess(savedTransaction);
            } catch (Exception e) {
                stats.updateError(lineNumber, e.getMessage());
                logger.warn("Error processing record at line {}: {}", lineNumber, e.getMessage());
            }
        }
        
        return stats;
    }
    
    /**
     * Convierte una línea del CSV en una transacción
     */
    private UserTransaction parseTransaction(String[] record, int lineNumber) {
        if (record.length != 4) {
            throw new IllegalArgumentException(
                    String.format("Invalid number of columns at line %d", lineNumber));
        }
        
        try {
            Integer id = Integer.parseInt(record[0]);
            Integer userId = Integer.parseInt(record[1]);
            Double amount = Double.parseDouble(record[2]);
            LocalDateTime dateTime = parseDateTime(record[3], lineNumber);
            
            return new UserTransaction(id, userId, amount, dateTime);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid number format at line %d: %s", lineNumber, e.getMessage()));
        }
    }
    
    /**
     * Parsea la fecha con múltiples formatos soportados
     */
    private LocalDateTime parseDateTime(String dateTimeStr, int lineNumber) {
        // Try different date formats
        try {
            // Try full datetime format first
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e1) {
            try {
                // Try ISO format
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                try {
                    // Try date only format
                    return LocalDateTime.parse(dateTimeStr + " 00:00:00", 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (DateTimeParseException e3) {
                    // If all fail, throw the first exception
                    throw new IllegalArgumentException("Invalid date format: " + dateTimeStr, e1);
                }
            }
        }
    }
    
    /**
     * Genera el reporte de migración basado en las estadísticas
     */
    private MigrationReport generateMigrationReport(MigrationStats stats, String filename, 
                                                 Long fileSize, Duration processingTime) {
        Double averageAmount = stats.successRecords > 0 ? 
                stats.totalAmount / stats.successRecords : 0.0;
        
        MigrationReport.DateRange dateRange = new MigrationReport.DateRange(
                stats.firstDate, stats.lastDate);
        
        return new MigrationReport(
                LocalDateTime.now(),
                filename,
                fileSize,
                stats.totalRecords,
                stats.successRecords,
                stats.errorRecords,
                processingTime,
                stats.usersAffected.size(),
                stats.totalAmount,
                averageAmount,
                stats.largestAmount,
                stats.smallestAmount,
                dateRange,
                stats.errors,
                null // TODO: Implementar generación de archivo CSV de errores
        );
    }
    
    /**
     * Clase interna para manejar estadísticas de migración
     */
    private static class MigrationStats {
        int totalRecords = 0;
        int successRecords = 0;
        int errorRecords = 0;
        Set<Integer> usersAffected = new HashSet<>();
        Double totalAmount = 0.0;
        Double largestAmount = Double.MIN_VALUE;
        Double smallestAmount = Double.MAX_VALUE;
        LocalDateTime firstDate = null;
        LocalDateTime lastDate = null;
        List<String> errors = new ArrayList<>();
        
        void updateSuccess(UserTransaction transaction) {
            successRecords++;
            usersAffected.add(transaction.getUserId());
            totalAmount += transaction.getAmount();
            
            // Actualizar montos
            if (transaction.getAmount() > largestAmount) {
                largestAmount = transaction.getAmount();
            }
            if (transaction.getAmount() < smallestAmount) {
                smallestAmount = transaction.getAmount();
            }
            
            // Actualizar fechas
            if (firstDate == null || transaction.getDateTime().isBefore(firstDate)) {
                firstDate = transaction.getDateTime();
            }
            if (lastDate == null || transaction.getDateTime().isAfter(lastDate)) {
                lastDate = transaction.getDateTime();
            }
        }
        
        void updateError(int lineNumber, String errorMessage) {
            errorRecords++;
            errors.add(String.format("Line %d: %s", lineNumber, errorMessage));
        }
    }
}
