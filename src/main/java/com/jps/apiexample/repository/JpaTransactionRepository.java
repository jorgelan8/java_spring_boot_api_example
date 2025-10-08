package com.jps.apiexample.repository;

import com.jps.apiexample.model.UserTransaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para transacciones de usuario
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jpa")
public interface JpaTransactionRepository extends JpaRepository<UserTransaction, Integer> {
    
    /**
     * Obtiene todas las transacciones de un usuario
     */
    List<UserTransaction> findByUserId(Integer userId);
    
    /**
     * Obtiene las transacciones de un usuario filtradas por rango de fechas
     */
    @Query("SELECT t FROM UserTransaction t WHERE t.userId = :userId " +
           "AND (:fromDate IS NULL OR t.dateTime >= :fromDate) " +
           "AND (:toDate IS NULL OR t.dateTime <= :toDate) " +
           "ORDER BY t.dateTime ASC")
    List<UserTransaction> findByUserIdAndDateRange(
            @Param("userId") Integer userId, 
            @Param("fromDate") LocalDateTime fromDate, 
            @Param("toDate") LocalDateTime toDate);
    
    /**
     * Cuenta las transacciones de un usuario
     */
    long countByUserId(Integer userId);
    
    /**
     * Obtiene el balance total de un usuario
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM UserTransaction t WHERE t.userId = :userId")
    Double getTotalBalanceByUserId(@Param("userId") Integer userId);
    
    /**
     * Obtiene el balance total de un usuario en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM UserTransaction t WHERE t.userId = :userId " +
           "AND (:fromDate IS NULL OR t.dateTime >= :fromDate) " +
           "AND (:toDate IS NULL OR t.dateTime <= :toDate)")
    Double getTotalBalanceByUserIdAndDateRange(
            @Param("userId") Integer userId, 
            @Param("fromDate") LocalDateTime fromDate, 
            @Param("toDate") LocalDateTime toDate);
    
    /**
     * Obtiene las transacciones más recientes de un usuario
     */
    @Query("SELECT t FROM UserTransaction t WHERE t.userId = :userId ORDER BY t.dateTime DESC")
    List<UserTransaction> findTop10ByUserIdOrderByDateTimeDesc(@Param("userId") Integer userId);
}
