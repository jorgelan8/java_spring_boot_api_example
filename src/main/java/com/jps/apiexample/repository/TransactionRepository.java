package com.jps.apiexample.repository;

import com.jps.apiexample.model.UserTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de transacciones
 */
public interface TransactionRepository {
    
    /**
     * Guarda una transacción en el repositorio
     */
    UserTransaction save(UserTransaction transaction);
    
    /**
     * Obtiene una transacción por ID
     */
    Optional<UserTransaction> findById(Integer id);
    
    /**
     * Obtiene todas las transacciones de un usuario
     */
    List<UserTransaction> findByUserId(Integer userId);
    
    /**
     * Obtiene las transacciones de un usuario filtradas por rango de fechas
     */
    List<UserTransaction> findByUserIdAndDateRange(Integer userId, LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Obtiene todas las transacciones
     */
    List<UserTransaction> findAll();
    
    /**
     * Retorna el número total de transacciones
     */
    int count();
    
    /**
     * Limpia todas las transacciones (útil para testing)
     */
    void clear();
}
