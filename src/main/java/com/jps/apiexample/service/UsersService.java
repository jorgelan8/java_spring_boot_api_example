package com.jps.apiexample.service;

import com.jps.apiexample.model.BalanceInfo;
import com.jps.apiexample.model.UserTransaction;
import com.jps.apiexample.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para manejar las operaciones de negocio relacionadas con usuarios
 */
@Service
public class UsersService {
    
    private final TransactionRepository transactionRepository;
    
    @Autowired
    public UsersService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Obtiene el balance de un usuario con filtros opcionales de fecha
     */
    public BalanceInfo getUserBalance(Integer userId, LocalDateTime fromDate, LocalDateTime toDate) {
        List<UserTransaction> userTransactions = transactionRepository
                .findByUserIdAndDateRange(userId, fromDate, toDate);
        
        if (userTransactions.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        
        BalanceCalculation calculation = calculateBalance(userTransactions);
        
        return new BalanceInfo(
                calculation.balance,
                calculation.totalDebits,
                calculation.totalCredits
        );
    }
    
    /**
     * Calcula el balance, total de débitos y créditos
     */
    private BalanceCalculation calculateBalance(List<UserTransaction> transactions) {
        double balance = 0.0;
        double totalDebits = 0.0;
        double totalCredits = 0.0;
        
        for (UserTransaction transaction : transactions) {
            balance += transaction.getAmount();
            
            if (transaction.getAmount() < 0) {
                totalDebits += transaction.getAmount();
            } else if (transaction.getAmount() > 0) {
                totalCredits += transaction.getAmount();
            }
        }
        
        return new BalanceCalculation(balance, totalDebits, totalCredits);
    }
    
    /**
     * Clase interna para encapsular los cálculos de balance
     */
    private static class BalanceCalculation {
        final double balance;
        final double totalDebits;
        final double totalCredits;
        
        BalanceCalculation(double balance, double totalDebits, double totalCredits) {
            this.balance = balance;
            this.totalDebits = totalDebits;
            this.totalCredits = totalCredits;
        }
    }
    
    /**
     * Excepción personalizada para usuario no encontrado
     */
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}
