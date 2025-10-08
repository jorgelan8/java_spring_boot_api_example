package com.jps.apiexample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para el health check de la aplicación
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Operaciones de monitoreo de salud de la aplicación")
public class HealthController {
    
    /**
     * Endpoint para verificar el estado de salud de la API
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica el estado de salud de la API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "spring_boot_api_example");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}
