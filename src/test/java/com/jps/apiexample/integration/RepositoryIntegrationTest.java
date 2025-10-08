package com.jps.apiexample.integration;

import com.jps.apiexample.model.UserTransaction;
import com.jps.apiexample.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para verificar el funcionamiento del repositorio
 * Configurable entre mock y persistente
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "app.repository.type=${TEST_REPOSITORY_TYPE:mock}",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
@Transactional
class RepositoryIntegrationTest {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @BeforeEach
    void setUp() {
        transactionRepository.clear();
    }
    
    @Test
    void testSaveAndFindById() {
        // Given
        UserTransaction transaction = new UserTransaction(
            null, 1, 100.50, LocalDateTime.now()
        );
        
        // When
        UserTransaction saved = transactionRepository.save(transaction);
        Optional<UserTransaction> found = transactionRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1, found.get().getUserId());
        assertEquals(100.50, found.get().getAmount());
    }
    
    @Test
    void testFindByUserId() {
        // Given
        UserTransaction transaction1 = new UserTransaction(null, 1, 100.0, LocalDateTime.now());
        UserTransaction transaction2 = new UserTransaction(null, 1, -50.0, LocalDateTime.now());
        UserTransaction transaction3 = new UserTransaction(null, 2, 200.0, LocalDateTime.now());
        
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        
        // When
        List<UserTransaction> user1Transactions = transactionRepository.findByUserId(1);
        List<UserTransaction> user2Transactions = transactionRepository.findByUserId(2);
        
        // Then
        assertEquals(2, user1Transactions.size());
        assertEquals(1, user2Transactions.size());
    }
    
    @Test
    void testFindByUserIdAndDateRange() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        
        UserTransaction oldTransaction = new UserTransaction(null, 1, 100.0, yesterday);
        UserTransaction currentTransaction = new UserTransaction(null, 1, 200.0, now);
        UserTransaction futureTransaction = new UserTransaction(null, 1, 300.0, tomorrow);
        
        transactionRepository.save(oldTransaction);
        transactionRepository.save(currentTransaction);
        transactionRepository.save(futureTransaction);
        
        // When
        List<UserTransaction> recentTransactions = transactionRepository
            .findByUserIdAndDateRange(1, now.minusHours(1), now.plusHours(1));
        
        // Then
        assertEquals(1, recentTransactions.size());
        assertEquals(200.0, recentTransactions.get(0).getAmount());
    }
    
    @Test
    void testCount() {
        // Given
        transactionRepository.save(new UserTransaction(null, 1, 100.0, LocalDateTime.now()));
        transactionRepository.save(new UserTransaction(null, 2, 200.0, LocalDateTime.now()));
        
        // When
        int count = transactionRepository.count();
        
        // Then
        assertEquals(2, count);
    }
    
    @Test
    void testClear() {
        // Given
        transactionRepository.save(new UserTransaction(null, 1, 100.0, LocalDateTime.now()));
        assertEquals(1, transactionRepository.count());
        
        // When
        transactionRepository.clear();
        
        // Then
        assertEquals(0, transactionRepository.count());
    }
}
