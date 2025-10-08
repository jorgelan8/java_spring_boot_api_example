package com.jps.apiexample.config;

import com.jps.apiexample.repository.JpaTransactionRepositoryImpl;
import com.jps.apiexample.repository.MockTransactionRepository;
import com.jps.apiexample.repository.TransactionRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de testing para seleccionar el tipo de repositorio
 */
@TestConfiguration
public class TestRepositoryConfig {
    
    /**
     * Configuración para tests con repositorio mock
     */
    @Configuration
    @Profile("test-mock")
    static class MockTestConfig {
        
        @Bean
        @Primary
        public TransactionRepository transactionRepository() {
            return new MockTransactionRepository();
        }
    }
    
    /**
     * Configuración para tests con repositorio JPA (H2)
     */
    @Configuration
    @Profile("test-persistent")
    static class JpaTestConfig {
        
        @Bean
        @Primary
        public TransactionRepository transactionRepository(JpaTransactionRepositoryImpl jpaTransactionRepository) {
            return jpaTransactionRepository;
        }
    }
    
    /**
     * Configuración para tests de integración con base de datos real
     */
    @Configuration
    @Profile("test-integration")
    static class IntegrationTestConfig {
        
        @Bean
        @Primary
        public TransactionRepository transactionRepository(JpaTransactionRepositoryImpl jpaTransactionRepository) {
            return jpaTransactionRepository;
        }
    }
}
