package com.jps.apiexample.repository;

import com.jps.apiexample.model.UserTransaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación mock del repositorio de transacciones usando memoria
 */
@Repository
public class MockTransactionRepository implements TransactionRepository {
    
    private final Map<Integer, UserTransaction> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);
    
    @Override
    public UserTransaction save(UserTransaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(nextId.getAndIncrement());
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }
    
    @Override
    public Optional<UserTransaction> findById(Integer id) {
        return Optional.ofNullable(transactions.get(id));
    }
    
    @Override
    public List<UserTransaction> findByUserId(Integer userId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserTransaction> findByUserIdAndDateRange(Integer userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getUserId().equals(userId))
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDateTime();
                    
                    // Si no hay filtros de fecha, incluir la transacción
                    if (fromDate == null && toDate == null) {
                        return true;
                    }
                    
                    // Verificar si la transacción está en el rango de fechas
                    boolean include = true;
                    
                    if (fromDate != null && transactionDate.isBefore(fromDate)) {
                        include = false;
                    }
                    
                    if (toDate != null && transactionDate.isAfter(toDate)) {
                        include = false;
                    }
                    
                    return include;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserTransaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
    
    @Override
    public int count() {
        return transactions.size();
    }
    
    @Override
    public void clear() {
        transactions.clear();
        nextId.set(1);
    }
}
