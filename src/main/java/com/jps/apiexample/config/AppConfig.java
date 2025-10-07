package com.jps.apiexample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la aplicación
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private String port = "8080";
    private String host = "localhost";
    private String environment = "development";
    
    // Getters and Setters
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    @Override
    public String toString() {
        return "AppConfig{" +
                "port='" + port + '\'' +
                ", host='" + host + '\'' +
                ", environment='" + environment + '\'' +
                '}';
    }
}
