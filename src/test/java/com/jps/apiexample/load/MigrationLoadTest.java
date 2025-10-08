package com.jps.apiexample.load;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de carga para el endpoint de migración
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class MigrationLoadTest extends TestServerUtils {
    
    private static final int CONCURRENCY = 10;
    private static final int REQUESTS_PER_THREAD = 25;
    private static final int TOTAL_REQUESTS = CONCURRENCY * REQUESTS_PER_THREAD;
    
    @Test
    void testLoadMigration() throws InterruptedException {
        // Arrange
        String csvData = CsvGenerator.generateTestCsv(100);
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        long startTime = System.currentTimeMillis();
        
        // Act - Ejecutar requests concurrentes
        for (int i = 0; i < CONCURRENCY; i++) {
            executor.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                    try {
                        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                                getMigrateUrl(), "csv_file", "load_test.csv", csvData);
                        
                        if (response.getStatusCode() == HttpStatus.OK) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                            errors.add(new RuntimeException("Unexpected status: " + response.getStatusCode()));
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        errors.add(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        
        // Wait for all requests to complete
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;
        
        executor.shutdown();
        
        // Assert
        assertTrue(completed, "Test timed out after 30 seconds");
        assertEquals(0, errorCount.get(), "Expected 0 errors, got " + errorCount.get() + 
                ". Errors: " + errors.stream().map(Exception::getMessage).toList());
        assertEquals(TOTAL_REQUESTS, successCount.get());
        
        // Log performance metrics
        System.out.printf("Load test completed in %d ms%n", duration);
        System.out.printf("Successful requests: %d%n", successCount.get());
        System.out.printf("Failed requests: %d%n", errorCount.get());
        System.out.printf("Requests per second: %.2f%n", (double) successCount.get() / (duration / 1000.0));
    }
    
    @Test
    void testLoadMigrationWithLargeFiles() throws InterruptedException {
        // Arrange
        String csvData = CsvGenerator.generateTestCsv(1000); // Archivo más grande
        ExecutorService executor = Executors.newFixedThreadPool(5); // Menos concurrencia para archivos grandes
        CountDownLatch latch = new CountDownLatch(25); // Menos requests totales
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        long startTime = System.currentTimeMillis();
        
        // Act
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    try {
                        ResponseEntity<String> response = HttpTestUtils.postMultipart(
                                getMigrateUrl(), "csv_file", "large_load_test.csv", csvData);
                        
                        if (response.getStatusCode() == HttpStatus.OK) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                            errors.add(new RuntimeException("Unexpected status: " + response.getStatusCode()));
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        errors.add(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        
        boolean completed = latch.await(60, TimeUnit.SECONDS); // Más tiempo para archivos grandes
        long duration = System.currentTimeMillis() - startTime;
        
        executor.shutdown();
        
        // Assert
        assertTrue(completed, "Test timed out after 60 seconds");
        assertEquals(0, errorCount.get(), "Expected 0 errors, got " + errorCount.get());
        assertEquals(25, successCount.get());
        
        // Log performance metrics
        System.out.printf("Large file load test completed in %d ms%n", duration);
        System.out.printf("Successful requests: %d%n", successCount.get());
        System.out.printf("Failed requests: %d%n", errorCount.get());
        System.out.printf("Requests per second: %.2f%n", (double) successCount.get() / (duration / 1000.0));
    }
}
