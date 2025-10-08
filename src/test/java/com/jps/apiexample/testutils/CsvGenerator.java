package com.jps.apiexample.testutils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Generador de datos CSV para pruebas
 */
public class CsvGenerator {
    
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Genera datos CSV para testing
     */
    public static String generateTestCsv(int recordCount) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,user_id,amount,datetime\n");
        
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        
        for (int i = 1; i <= recordCount; i++) {
            int userId = 1001 + ((i - 1) % 100); // 100 diferentes usuarios, empezando desde 1001
            double amount = (i % 1000) - 500.0; // Montos entre -500 y 499
            LocalDateTime dateTime = baseTime.plusHours(i);
            
            csv.append(String.format("%d,%d,%.2f,%s\n",
                    i, userId, amount, dateTime.format(DATE_FORMATTER)));
        }
        
        return csv.toString();
    }
    
    /**
     * Genera datos CSV con distribución específica de usuarios
     */
    public static String generateTestCsvWithUsers(int recordCount, int userCount) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,user_id,amount,datetime\n");
        
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        
        for (int i = 1; i <= recordCount; i++) {
            int userId = 1001 + (i % userCount); // Distribuir entre el número especificado de usuarios
            double amount = (i % 1000) - 500.0; // Montos entre -500 y 499
            LocalDateTime dateTime = baseTime.plusHours(i);
            
            csv.append(String.format("%d,%d,%.2f,%s\n",
                    i, userId, amount, dateTime.format(DATE_FORMATTER)));
        }
        
        return csv.toString();
    }
    
    /**
     * Genera datos CSV con fechas específicas
     */
    public static String generateTestCsvWithDateRange(int recordCount, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,user_id,amount,datetime\n");
        
        long hoursBetween = java.time.Duration.between(startDate, endDate).toHours();
        
        for (int i = 1; i <= recordCount; i++) {
            int userId = 1001 + (i % 10); // 10 diferentes usuarios
            double amount = (i % 1000) - 500.0; // Montos entre -500 y 499
            LocalDateTime dateTime = startDate.plusHours((i * hoursBetween) / recordCount);
            
            csv.append(String.format("%d,%d,%.2f,%s\n",
                    i, userId, amount, dateTime.format(DATE_FORMATTER)));
        }
        
        return csv.toString();
    }
    
    /**
     * Genera datos CSV con errores para testing de validación
     */
    public static String generateInvalidCsv(int recordCount) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,user_id,amount,datetime\n");
        
        for (int i = 1; i <= recordCount; i++) {
            if (i % 3 == 0) {
                // Cada tercer registro tiene un error
                csv.append(String.format("%d,invalid_user,invalid_amount,invalid_date\n", i));
            } else {
                int userId = 1001 + (i % 10);
                double amount = (i % 1000) - 500.0;
                LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 12, 0, 0).plusHours(i);
                
                csv.append(String.format("%d,%d,%.2f,%s\n",
                        i, userId, amount, dateTime.format(DATE_FORMATTER)));
            }
        }
        
        return csv.toString();
    }
    
    /**
     * Genera datos CSV con diferentes formatos de fecha
     */
    public static String generateCsvWithDifferentDateFormats(int recordCount) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,user_id,amount,datetime\n");
        
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        
        for (int i = 1; i <= recordCount; i++) {
            int userId = 1001 + (i % 10);
            double amount = (i % 1000) - 500.0;
            LocalDateTime dateTime = baseTime.plusHours(i);
            
            String dateFormat;
            if (i % 3 == 0) {
                // Formato con T
                dateFormat = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            } else if (i % 3 == 1) {
                // Formato solo fecha
                dateFormat = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                // Formato estándar
                dateFormat = dateTime.format(DATE_FORMATTER);
            }
            
            csv.append(String.format("%d,%d,%.2f,%s\n", i, userId, amount, dateFormat));
        }
        
        return csv.toString();
    }
}
