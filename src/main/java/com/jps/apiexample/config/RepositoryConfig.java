package com.jps.apiexample.config;

import com.jps.apiexample.repository.JpaTransactionRepositoryImpl;
import com.jps.apiexample.repository.MockTransactionRepository;
import com.jps.apiexample.repository.TransactionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración condicional para seleccionar el tipo de repositorio en runtime
 */
@Configuration
public class RepositoryConfig {
    
    /**
     * Configuración para usar repositorio mock
     */
    @Configuration
    @ConditionalOnProperty(name = "app.repository.type", havingValue = "mock", matchIfMissing = true)
    static class MockRepositoryConfig {
        
        @Bean
        @Primary
        public TransactionRepository transactionRepository() {
            return new MockTransactionRepository();
        }
    }
    
    /**
     * Configuración para usar repositorio JPA
     */
    @Configuration
    @ConditionalOnProperty(name = "app.repository.type", havingValue = "jpa")
    static class JpaRepositoryConfig {
        
        @Bean
        @Primary
        public TransactionRepository transactionRepository(JpaTransactionRepositoryImpl jpaTransactionRepository) {
            return jpaTransactionRepository;
        }
    }
}
