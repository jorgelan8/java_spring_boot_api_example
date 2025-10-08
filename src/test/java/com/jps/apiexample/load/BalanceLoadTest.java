package com.jps.apiexample.load;

import com.jps.apiexample.testutils.TestConfig;
import com.jps.apiexample.testutils.CsvGenerator;
import com.jps.apiexample.testutils.HttpTestUtils;
import com.jps.apiexample.testutils.TestServerUtils;
import org.junit.jupiter.api.BeforeEach;
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
 * Pruebas de carga para el endpoint de balance
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class BalanceLoadTest extends TestServerUtils {
    
    private static final int CONCURRENCY = 10;
    private static final int REQUESTS_PER_THREAD = 250;
    private static final int TOTAL_REQUESTS = CONCURRENCY * REQUESTS_PER_THREAD;
    
    @BeforeEach
    void setUp() {
        // Migrar datos de prueba antes de cada test
        String csvContent = CsvGenerator.generateTestCsv(50);
        HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "test.csv", csvContent);
    }
    
    @Test
    void testLoadBalance() throws InterruptedException {
        // Arrange
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        long startTime = System.currentTimeMillis();
        
        // Act - Ejecutar requests concurrentes
        for (int i = 0; i < CONCURRENCY; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                    try {
                        // Test different user IDs
                        int userId = 1001 + ((threadId * REQUESTS_PER_THREAD + j) % 10);
                        ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(userId));
                        
                        if (response.getStatusCode() == HttpStatus.OK || 
                            response.getStatusCode() == HttpStatus.BAD_REQUEST) {
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
        System.out.printf("Balance load test completed in %d ms%n", duration);
        System.out.printf("Successful requests: %d%n", successCount.get());
        System.out.printf("Failed requests: %d%n", errorCount.get());
        System.out.printf("Requests per second: %.2f%n", (double) successCount.get() / (duration / 1000.0));
    }
    
    @Test
    void testLoadBalanceWithDateRange() throws InterruptedException {
        // Arrange
        ExecutorService executor = Executors.newFixedThreadPool(15); // Más concurrencia
        CountDownLatch latch = new CountDownLatch(3750); // 15 * 250
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        long startTime = System.currentTimeMillis();
        
        // Act - Ejecutar requests concurrentes con rango de fechas
        for (int i = 0; i < 15; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 250; j++) {
                    try {
                        // Test different user IDs and date ranges
                        int userId = 1001 + ((threadId * 250 + j) % 10);
                        String fromDate = "2024-01-15T00:00:00Z";
                        String toDate = "2024-01-20T23:59:59Z";
                        String url = getBalanceUrlWithDateRange(userId, fromDate, toDate);
                        
                        ResponseEntity<String> response = HttpTestUtils.get(url);
                        
                        if (response.getStatusCode() == HttpStatus.OK || 
                            response.getStatusCode() == HttpStatus.BAD_REQUEST) {
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
        assertEquals(0, errorCount.get(), "Expected 0 errors, got " + errorCount.get());
        assertEquals(3750, successCount.get());
        
        // Log performance metrics
        System.out.printf("Balance with date range load test completed in %d ms%n", duration);
        System.out.printf("Successful requests: %d%n", successCount.get());
        System.out.printf("Failed requests: %d%n", errorCount.get());
        System.out.printf("Requests per second: %.2f%n", (double) successCount.get() / (duration / 1000.0));
    }
    
    @Test
    void testLoadBalanceConcurrentUsers() throws InterruptedException {
        // Arrange
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(1000);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        
        long startTime = System.currentTimeMillis();
        
        // Act - Test con muchos usuarios concurrentes
        for (int i = 0; i < 20; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        // Test different user IDs
                        int userId = 1001 + ((threadId * 50 + j) % 20);
                        ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(userId));
                        
                        if (response.getStatusCode() == HttpStatus.OK || 
                            response.getStatusCode() == HttpStatus.BAD_REQUEST) {
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
        assertEquals(0, errorCount.get(), "Expected 0 errors, got " + errorCount.get());
        assertEquals(1000, successCount.get());
        
        // Log performance metrics
        System.out.printf("Concurrent users load test completed in %d ms%n", duration);
        System.out.printf("Successful requests: %d%n", successCount.get());
        System.out.printf("Failed requests: %d%n", errorCount.get());
        System.out.printf("Requests per second: %.2f%n", (double) successCount.get() / (duration / 1000.0));
    }
}
