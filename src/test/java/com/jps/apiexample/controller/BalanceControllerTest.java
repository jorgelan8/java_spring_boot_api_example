package com.jps.apiexample.controller;

import com.jps.apiexample.model.BalanceInfo;
import com.jps.apiexample.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para BalanceController
 */
@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {
    
    @Mock
    private UsersService usersService;
    
    @InjectMocks
    private BalanceController balanceController;
    
    private BalanceInfo mockBalanceInfo;
    private LocalDateTime testFromDate;
    private LocalDateTime testToDate;
    
    @BeforeEach
    void setUp() {
        mockBalanceInfo = new BalanceInfo(75.25, -75.25, 150.50);
        testFromDate = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
        testToDate = LocalDateTime.of(2024, 1, 20, 23, 59, 59);
    }
    
    @Test
    void testGetUserBalance_ValidUser_ShouldReturnOk() {
        // Arrange
        when(usersService.getUserBalance(1001, null, null)).thenReturn(mockBalanceInfo);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, null, null);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(75.25, response.getBody().getBalance(), 0.01);
        assertEquals(-75.25, response.getBody().getTotalDebits(), 0.01);
        assertEquals(150.50, response.getBody().getTotalCredits(), 0.01);
        
        verify(usersService).getUserBalance(1001, null, null);
    }
    
    @Test
    void testGetUserBalance_WithDateRange_ShouldReturnOk() {
        // Arrange
        when(usersService.getUserBalance(1001, testFromDate, testToDate)).thenReturn(mockBalanceInfo);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, testFromDate, testToDate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(usersService).getUserBalance(1001, testFromDate, testToDate);
    }
    
    @Test
    void testGetUserBalance_UserNotFound_ShouldReturnBadRequest() {
        // Arrange
        when(usersService.getUserBalance(9999, null, null))
                .thenThrow(new UsersService.UserNotFoundException("User not found"));
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(9999, null, null);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(usersService).getUserBalance(9999, null, null);
    }
    
    @Test
    void testGetUserBalance_InvalidDateRange_ShouldReturnBadRequest() {
        // Arrange - fromDate is after toDate
        LocalDateTime invalidFromDate = testToDate.plusDays(1);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, invalidFromDate, testToDate);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(usersService, never()).getUserBalance(any(), any(), any());
    }
    
    @Test
    void testGetUserBalance_ServiceException_ShouldReturnInternalServerError() {
        // Arrange
        when(usersService.getUserBalance(1001, null, null))
                .thenThrow(new RuntimeException("Internal error"));
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, null, null);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(usersService).getUserBalance(1001, null, null);
    }
    
    @Test
    void testGetUserBalance_OnlyFromDate_ShouldReturnOk() {
        // Arrange
        when(usersService.getUserBalance(1001, testFromDate, null)).thenReturn(mockBalanceInfo);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, testFromDate, null);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(usersService).getUserBalance(1001, testFromDate, null);
    }
    
    @Test
    void testGetUserBalance_OnlyToDate_ShouldReturnOk() {
        // Arrange
        when(usersService.getUserBalance(1001, null, testToDate)).thenReturn(mockBalanceInfo);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, null, testToDate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(usersService).getUserBalance(1001, null, testToDate);
    }
    
    @Test
    void testGetUserBalance_EqualDates_ShouldReturnOk() {
        // Arrange
        LocalDateTime sameDate = LocalDateTime.of(2024, 1, 15, 12, 0, 0);
        when(usersService.getUserBalance(1001, sameDate, sameDate)).thenReturn(mockBalanceInfo);
        
        // Act
        ResponseEntity<BalanceInfo> response = balanceController.getUserBalance(1001, sameDate, sameDate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(usersService).getUserBalance(1001, sameDate, sameDate);
    }
}
