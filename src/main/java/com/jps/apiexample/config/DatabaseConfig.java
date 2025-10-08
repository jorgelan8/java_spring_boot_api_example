package com.jps.apiexample.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Configuración condicional para deshabilitar componentes de base de datos
 * cuando se usa el repositorio mock
 */
@Configuration
@ConditionalOnProperty(name = "app.repository.type", havingValue = "mock")
public class DatabaseConfig {
    
    /**
     * Deshabilita la configuración automática de base de datos cuando se usa mock
     */
    @Configuration
    @ConditionalOnProperty(name = "app.repository.type", havingValue = "mock")
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
    })
    static class DisableDatabaseAutoConfiguration {
        // Esta clase deshabilita la configuración automática de base de datos
    }
}
