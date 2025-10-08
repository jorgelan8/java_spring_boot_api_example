package com.jps.apiexample.service;

import com.jps.apiexample.model.BalanceInfo;
import com.jps.apiexample.model.UserTransaction;
import com.jps.apiexample.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsersService
 */
@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @InjectMocks
    private UsersService usersService;
    
    private List<UserTransaction> testTransactions;
    private LocalDateTime baseTime;
    
    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        
        testTransactions = Arrays.asList(
                new UserTransaction(1, 1001, 150.50, baseTime),
                new UserTransaction(2, 1001, -75.25, baseTime.plusHours(24)),
                new UserTransaction(3, 1002, 200.00, baseTime)
        );
    }
    
    @Test
    void testGetUserBalance_ValidUser_ShouldReturnCorrectBalance() {
        // Arrange
        List<UserTransaction> userTransactions = Arrays.asList(
                testTransactions.get(0), // 150.50
                testTransactions.get(1)  // -75.25
        );
        when(transactionRepository.findByUserIdAndDateRange(1001, null, null))
                .thenReturn(userTransactions);
        
        // Act
        BalanceInfo balanceInfo = usersService.getUserBalance(1001, null, null);
        
        // Assert
        assertNotNull(balanceInfo);
        assertEquals(75.25, balanceInfo.getBalance(), 0.01); // 150.50 - 75.25
        assertEquals(-75.25, balanceInfo.getTotalDebits(), 0.01);
        assertEquals(150.50, balanceInfo.getTotalCredits(), 0.01);
        
        verify(transactionRepository).findByUserIdAndDateRange(1001, null, null);
    }
    
    @Test
    void testGetUserBalance_UserNotFound_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findByUserIdAndDateRange(9999, null, null))
                .thenReturn(Collections.emptyList());
        
        // Act & Assert
        UsersService.UserNotFoundException exception = assertThrows(
                UsersService.UserNotFoundException.class,
                () -> usersService.getUserBalance(9999, null, null)
        );
        
        assertEquals("User not found", exception.getMessage());
        verify(transactionRepository).findByUserIdAndDateRange(9999, null, null);
    }
    
    @Test
    void testGetUserBalance_WithDateRange_ShouldReturnFilteredBalance() {
        // Arrange
        LocalDateTime fromDate = baseTime.plusHours(12);
        LocalDateTime toDate = baseTime.plusHours(36);
        
        List<UserTransaction> filteredTransactions = Arrays.asList(
                testTransactions.get(1) // -75.25 (within range)
        );
        
        when(transactionRepository.findByUserIdAndDateRange(1001, fromDate, toDate))
                .thenReturn(filteredTransactions);
        
        // Act
        BalanceInfo balanceInfo = usersService.getUserBalance(1001, fromDate, toDate);
        
        // Assert
        assertNotNull(balanceInfo);
        assertEquals(-75.25, balanceInfo.getBalance(), 0.01);
        assertEquals(-75.25, balanceInfo.getTotalDebits(), 0.01);
        assertEquals(0.0, balanceInfo.getTotalCredits(), 0.01);
        
        verify(transactionRepository).findByUserIdAndDateRange(1001, fromDate, toDate);
    }
    
    @Test
    void testGetUserBalance_OnlyFromDate_ShouldReturnCorrectBalance() {
        // Arrange
        LocalDateTime fromDate = baseTime.plusHours(12);
        
        List<UserTransaction> filteredTransactions = Arrays.asList(
                testTransactions.get(1) // -75.25
        );
        
        when(transactionRepository.findByUserIdAndDateRange(1001, fromDate, null))
                .thenReturn(filteredTransactions);
        
        // Act
        BalanceInfo balanceInfo = usersService.getUserBalance(1001, fromDate, null);
        
        // Assert
        assertNotNull(balanceInfo);
        assertEquals(-75.25, balanceInfo.getBalance(), 0.01);
        
        verify(transactionRepository).findByUserIdAndDateRange(1001, fromDate, null);
    }
    
    @Test
    void testGetUserBalance_OnlyToDate_ShouldReturnCorrectBalance() {
        // Arrange
        LocalDateTime toDate = baseTime.plusHours(12);
        
        List<UserTransaction> filteredTransactions = Arrays.asList(
                testTransactions.get(0) // 150.50
        );
        
        when(transactionRepository.findByUserIdAndDateRange(1001, null, toDate))
                .thenReturn(filteredTransactions);
        
        // Act
        BalanceInfo balanceInfo = usersService.getUserBalance(1001, null, toDate);
        
        // Assert
        assertNotNull(balanceInfo);
        assertEquals(150.50, balanceInfo.getBalance(), 0.01);
        
        verify(transactionRepository).findByUserIdAndDateRange(1001, null, toDate);
    }
    
    @Test
    void testGetUserBalance_ZeroAmountTransactions_ShouldHandleCorrectly() {
        // Arrange
        List<UserTransaction> transactionsWithZero = Arrays.asList(
                new UserTransaction(1, 1001, 0.0, baseTime),
                new UserTransaction(2, 1001, 100.0, baseTime.plusHours(1)),
                new UserTransaction(3, 1001, -50.0, baseTime.plusHours(2))
        );
        
        when(transactionRepository.findByUserIdAndDateRange(1001, null, null))
                .thenReturn(transactionsWithZero);
        
        // Act
        BalanceInfo balanceInfo = usersService.getUserBalance(1001, null, null);
        
        // Assert
        assertNotNull(balanceInfo);
        assertEquals(50.0, balanceInfo.getBalance(), 0.01); // 0 + 100 - 50
        assertEquals(-50.0, balanceInfo.getTotalDebits(), 0.01);
        assertEquals(100.0, balanceInfo.getTotalCredits(), 0.01);
    }
}
