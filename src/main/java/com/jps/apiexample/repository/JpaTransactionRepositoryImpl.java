package com.jps.apiexample.repository;

import com.jps.apiexample.model.UserTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio JPA que implementa la interfaz TransactionRepository
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jpa")
public class JpaTransactionRepositoryImpl implements TransactionRepository {
    
    private final JpaTransactionRepository jpaRepository;
    
    @Autowired
    public JpaTransactionRepositoryImpl(JpaTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public UserTransaction save(UserTransaction transaction) {
        return jpaRepository.save(transaction);
    }
    
    @Override
    public Optional<UserTransaction> findById(Integer id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public List<UserTransaction> findByUserId(Integer userId) {
        return jpaRepository.findByUserId(userId);
    }
    
    @Override
    public List<UserTransaction> findByUserIdAndDateRange(Integer userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return jpaRepository.findByUserIdAndDateRange(userId, fromDate, toDate);
    }
    
    @Override
    public List<UserTransaction> findAll() {
        return jpaRepository.findAll();
    }
    
    @Override
    public int count() {
        return (int) jpaRepository.count();
    }
    
    @Override
    public void clear() {
        jpaRepository.deleteAll();
    }
}
