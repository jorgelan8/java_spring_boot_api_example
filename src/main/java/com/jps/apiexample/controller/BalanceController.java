package com.jps.apiexample.controller;

import com.jps.apiexample.model.BalanceInfo;
import com.jps.apiexample.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controlador REST para manejar las consultas de balance de usuarios
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Balance", description = "Operaciones de consulta de balance de usuarios")
public class BalanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    
    private final UsersService usersService;
    
    @Autowired
    public BalanceController(UsersService usersService) {
        this.usersService = usersService;
    }
    
    /**
     * Endpoint para obtener el balance de un usuario
     */
    @GetMapping("/users/{user_id}/balance")
    @Operation(summary = "Obtener balance de usuario", 
               description = "Obtiene el balance de un usuario con filtros opcionales de fecha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance obtenido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Usuario no encontrado o parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BalanceInfo> getUserBalance(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable("user_id") Integer userId,
            
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate) {
        
        try {
            // Validar que from sea anterior a to si ambos se proporcionan
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                logger.warn("Invalid date range: from {} is after to {}", fromDate, toDate);
                return ResponseEntity.badRequest().build();
            }
            
            logger.info("Getting balance for user {} with date range: {} to {}", userId, fromDate, toDate);
            
            // Obtener balance del usuario
            BalanceInfo balanceInfo = usersService.getUserBalance(userId, fromDate, toDate);
            
            logger.info("Balance retrieved successfully for user {}: balance={}, debits={}, credits={}", 
                    userId, balanceInfo.getBalance(), balanceInfo.getTotalDebits(), balanceInfo.getTotalCredits());
            
            return ResponseEntity.ok(balanceInfo);
            
        } catch (UsersService.UserNotFoundException e) {
            logger.warn("User not found: {}", userId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting balance for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
