package com.jps.apiexample.performance;

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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de performance para el endpoint de balance
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class BalancePerformanceTest extends TestServerUtils {
    
    @BeforeEach
    void setUp() {
        // Migrar datos de prueba antes de cada test
        String csvContent = CsvGenerator.generateTestCsv(100);
        HttpTestUtils.postMultipart(getMigrateUrl(), "csv_file", "test.csv", csvContent);
    }
    
    @Test
    void testPerformanceBalance_DifferentUserCounts() {
        int[] userCounts = {1, 10, 50, 100};
        
        for (int userCount : userCounts) {
            System.out.printf("Testing with %d users%n", userCount);
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < userCount; i++) {
                int userId = 1001 + (i % 10);
                ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(userId));
                
                // Accept both OK and BadRequest (user not found)
                assertTrue(response.getStatusCode() == HttpStatus.OK || 
                          response.getStatusCode() == HttpStatus.BAD_REQUEST);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Log performance metrics
            double requestsPerSecond = (double) userCount / (duration / 1000.0);
            System.out.printf("User count: %d, Duration: %d ms, Requests/sec: %.2f%n",
                    userCount, duration, requestsPerSecond);
        }
    }
    
    @Test
    void testPerformanceBalance_DifferentDateRanges() {
        String[][] dateRanges = {
                {"1_Day", "2024-01-15T00:00:00Z", "2024-01-15T23:59:59Z"},
                {"3_Days", "2024-01-15T00:00:00Z", "2024-01-17T23:59:59Z"},
                {"7_Days", "2024-01-15T00:00:00Z", "2024-01-21T23:59:59Z"},
                {"30_Days", "2024-01-01T00:00:00Z", "2024-01-30T23:59:59Z"}
        };
        
        for (String[] dateRange : dateRanges) {
            String name = dateRange[0];
            String fromDate = dateRange[1];
            String toDate = dateRange[2];
            
            System.out.printf("Testing date range: %s%n", name);
            
            long startTime = System.currentTimeMillis();
            
            // Test with 50 requests
            for (int i = 0; i < 50; i++) {
                int userId = 1001 + (i % 10);
                String url = getBalanceUrlWithDateRange(userId, fromDate, toDate);
                ResponseEntity<String> response = HttpTestUtils.get(url);
                
                // Accept both OK and BadRequest (user not found)
                assertTrue(response.getStatusCode() == HttpStatus.OK || 
                          response.getStatusCode() == HttpStatus.BAD_REQUEST);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Log performance metrics
            double requestsPerSecond = 50.0 / (duration / 1000.0);
            System.out.printf("Date range: %s, Duration: %d ms, Requests/sec: %.2f%n",
                    name, duration, requestsPerSecond);
        }
    }
    
    @Test
    void testPerformanceBalance_ConcurrentRequests() throws Exception {
        int[] concurrencyLevels = {1, 5, 10, 20, 50};
        
        for (int concurrency : concurrencyLevels) {
            System.out.printf("Testing concurrency level: %d%n", concurrency);
            
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            
            long startTime = System.currentTimeMillis();
            
            // Submit concurrent requests
            CompletableFuture<ResponseEntity<String>>[] futures = new CompletableFuture[concurrency];
            for (int i = 0; i < concurrency; i++) {
                final int userId = 1001 + (i % 10);
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    return HttpTestUtils.get(getBalanceUrl(userId));
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
                if (response.getStatusCode() == HttpStatus.OK || 
                    response.getStatusCode() == HttpStatus.BAD_REQUEST) {
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
    void testPerformanceBalance_Throughput() throws Exception {
        // Test throughput with sustained load
        int totalRequests = 1000;
        int concurrency = 20;
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        
        long startTime = System.currentTimeMillis();
        
        // Submit requests in batches
        for (int batch = 0; batch < totalRequests / concurrency; batch++) {
            CompletableFuture<ResponseEntity<String>>[] futures = new CompletableFuture[concurrency];
            for (int i = 0; i < concurrency; i++) {
                final int userId = 1001 + ((batch * concurrency + i) % 10);
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    return HttpTestUtils.get(getBalanceUrl(userId));
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
    
    @Test
    void testPerformanceBalance_MemoryUsage() {
        // Test memory usage with many requests
        int requestCount = 1000;
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < requestCount; i++) {
            int userId = 1001 + (i % 10);
            ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(userId));
            
            // Accept both OK and BadRequest (user not found)
            assertTrue(response.getStatusCode() == HttpStatus.OK || 
                      response.getStatusCode() == HttpStatus.BAD_REQUEST);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Log performance metrics
        double requestsPerSecond = (double) requestCount / (duration / 1000.0);
        System.out.printf("Memory usage test: %d requests in %d ms%n", requestCount, duration);
        System.out.printf("Requests per second: %.2f%n", requestsPerSecond);
        
        // Log memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        System.out.printf("Memory usage: %d MB%n", usedMemory / (1024 * 1024));
    }
    
    @Test
    void testPerformanceBalance_ResponseTime() {
        // Test response time consistency
        int requestCount = 100;
        long[] responseTimes = new long[requestCount];
        
        for (int i = 0; i < requestCount; i++) {
            int userId = 1001 + (i % 10);
            
            long startTime = System.nanoTime();
            ResponseEntity<String> response = HttpTestUtils.get(getBalanceUrl(userId));
            long endTime = System.nanoTime();
            
            responseTimes[i] = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            // Accept both OK and BadRequest (user not found)
            assertTrue(response.getStatusCode() == HttpStatus.OK || 
                      response.getStatusCode() == HttpStatus.BAD_REQUEST);
        }
        
        // Calculate statistics
        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        
        for (long time : responseTimes) {
            sum += time;
            min = Math.min(min, time);
            max = Math.max(max, time);
        }
        
        double average = (double) sum / requestCount;
        
        // Log performance metrics
        System.out.printf("Response time test: %d requests%n", requestCount);
        System.out.printf("Average response time: %.2f ms%n", average);
        System.out.printf("Min response time: %d ms%n", min);
        System.out.printf("Max response time: %d ms%n", max);
    }
}
