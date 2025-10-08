package com.jps.apiexample.performance;

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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de performance para el endpoint de migración
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class MigrationPerformanceTest extends TestServerUtils {
    
    @Test
    void testPerformanceMigration_DifferentCsvSizes() {
        int[] sizes = {12, 100, 1000, 5000};
        
        for (int size : sizes) {
            System.out.printf("Testing CSV size: %d records%n", size);
            
            String csvData = CsvGenerator.generateTestCsv(size);
            
            long startTime = System.currentTimeMillis();
            
            ResponseEntity<String> response = HttpTestUtils.postMultipart(
                    getMigrateUrl(), "csv_file", "perf_test.csv", csvData);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            
            // Log performance metrics
            double recordsPerSecond = (double) size / (duration / 1000.0);
            System.out.printf("CSV size: %d records, Duration: %d ms, Records/sec: %.2f%n",
                    size, duration, recordsPerSecond);
        }
    }
    
    @Test
    void testPerformanceMigration_MemoryUsage() {
        // Test with large CSV to check memory usage
        String csvData = CsvGenerator.generateTestCsv(10000); // 10,000 records
        
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                getMigrateUrl(), "csv_file", "memory_test.csv", csvData);
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Log performance metrics
        double recordsPerSecond = 10000.0 / (duration / 1000.0);
        System.out.printf("Large CSV (10,000 records) processed in %d ms%n", duration);
        System.out.printf("Records per second: %.2f%n", recordsPerSecond);
        
        // Log memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        System.out.printf("Memory usage: %d MB%n", usedMemory / (1024 * 1024));
    }
    
    @Test
    void testPerformanceMigration_ConcurrentRequests() throws Exception {
        int[] concurrencyLevels = {1, 5, 10, 20, 50};
        
        for (int concurrency : concurrencyLevels) {
            System.out.printf("Testing concurrency level: %d%n", concurrency);
            
            String csvData = CsvGenerator.generateTestCsv(100);
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            
            long startTime = System.currentTimeMillis();
            
            // Submit concurrent requests
            CompletableFuture<ResponseEntity<String>>[] futures = new CompletableFuture[concurrency];
            for (int i = 0; i < concurrency; i++) {
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    return HttpTestUtils.postMultipart(
                            getMigrateUrl(), "csv_file", "concurrent_test.csv", csvData);
                }, executor);
            }
            
            // Wait for all requests to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures);
            allFutures.get(30, TimeUnit.SECONDS);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Check all responses
            int successCount = 0;
            for (CompletableFuture<ResponseEntity<String>> future : futures) {
                ResponseEntity<String> response = future.get();
                if (response.getStatusCode() == HttpStatus.OK) {
                    successCount++;
                }
            }
            
            executor.shutdown();
            
            // Assert
            assertEquals(concurrency, successCount);
            
            // Log performance metrics
            double requestsPerSecond = (double) concurrency / (duration / 1000.0);
            System.out.printf("Concurrency: %d, Duration: %d ms, Success: %d, Requests/sec: %.2f%n",
                    concurrency, duration, successCount, requestsPerSecond);
        }
    }
    
    @Test
    void testPerformanceMigration_DifferentDateFormats() {
        String csvData = CsvGenerator.generateCsvWithDifferentDateFormats(1000);
        
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                getMigrateUrl(), "csv_file", "date_formats_test.csv", csvData);
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Log performance metrics
        double recordsPerSecond = 1000.0 / (duration / 1000.0);
        System.out.printf("Different date formats CSV processed in %d ms%n", duration);
        System.out.printf("Records per second: %.2f%n", recordsPerSecond);
    }
    
    @Test
    void testPerformanceMigration_Throughput() throws Exception {
        // Test throughput with sustained load
        int totalRequests = 100;
        int concurrency = 10;
        String csvData = CsvGenerator.generateTestCsv(50);
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        
        long startTime = System.currentTimeMillis();
        
        // Submit requests in batches
        for (int batch = 0; batch < totalRequests / concurrency; batch++) {
            CompletableFuture<ResponseEntity<String>>[] futures = new CompletableFuture[concurrency];
            for (int i = 0; i < concurrency; i++) {
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    return HttpTestUtils.postMultipart(
                            getMigrateUrl(), "csv_file", "throughput_test.csv", csvData);
                }, executor);
            }
            
            // Wait for batch to complete
            CompletableFuture.allOf(futures).get();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        executor.shutdown();
        
        // Log performance metrics
        double requestsPerSecond = (double) totalRequests / (duration / 1000.0);
        System.out.printf("Throughput test: %d requests in %d ms%n", totalRequests, duration);
        System.out.printf("Requests per second: %.2f%n", requestsPerSecond);
    }
}
