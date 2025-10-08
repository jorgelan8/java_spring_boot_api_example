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
 * Controlador REST para el endpoint raíz de la API
 */
@RestController
@RequestMapping("/")
@Tag(name = "Root", description = "Información general de la API")
public class RootController {
    
    /**
     * Endpoint raíz que proporciona información sobre la API
     */
    @GetMapping
    @Operation(summary = "API Information", description = "Obtiene información general sobre la API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información de la API")
    })
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API Example - Migration & Balance Service");
        response.put("version", "1.0.0");
        response.put("language", "Java Spring Boot");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("migrate", "POST /api/v1/migrate");
        endpoints.put("balance", "GET /api/v1/users/{user_id}/balance");
        endpoints.put("health", "GET /api/v1/health");
        response.put("endpoints", endpoints);
        
        Map<String, String> documentation = new HashMap<>();
        documentation.put("swagger_ui", "/api/v1/docs");
        documentation.put("openapi_yaml", "/api/v1/swagger.yaml");
        documentation.put("openapi_json", "/api/v1/swagger.json");
        response.put("documentation", documentation);
        
        return ResponseEntity.ok(response);
    }
}
