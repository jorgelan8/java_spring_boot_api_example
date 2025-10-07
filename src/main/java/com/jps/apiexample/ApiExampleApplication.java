package com.jps.apiexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal de API Example - Migration & Balance Service
 * 
 * Esta aplicación proporciona endpoints REST para:
 * - Migración de transacciones desde archivos CSV
 * - Consulta de balance de usuarios con filtros de fecha
 * - Reportes automáticos por email después de la migración
 * - Documentación OpenAPI (Swagger UI)
 */
@SpringBootApplication
@EnableAsync
public class ApiExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiExampleApplication.class, args);
        
        System.out.println("🚀 API Example Server started successfully!");
        System.out.println("📊 Available endpoints:");
        System.out.println("   POST /api/v1/migrate - Upload CSV file");
        System.out.println("   GET  /api/v1/users/{user_id}/balance - Get user balance");
        System.out.println("   GET  /api/v1/health - Health check");
        System.out.println("   GET  /api/v1/docs - Swagger UI documentation");
        System.out.println("📧 Email reports: Configured via application.yml");
    }
}
