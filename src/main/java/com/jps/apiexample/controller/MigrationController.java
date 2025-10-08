package com.jps.apiexample.controller;

import com.jps.apiexample.model.MigrationReport;
import com.jps.apiexample.service.MigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para manejar las operaciones de migración
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Migration", description = "Operaciones de migración de transacciones")
public class MigrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationController.class);
    
    private final MigrationService migrationService;
    
    @Autowired
    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }
    
    /**
     * Endpoint para subir y procesar archivo CSV de transacciones
     */
    @PostMapping(value = "/migrate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Migrar transacciones desde archivo CSV", 
               description = "Sube y procesa un archivo CSV con transacciones de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Migración exitosa"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o formato incorrecto"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> migrateCsv(@RequestParam("csv_file") MultipartFile file) {
        try {
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                logger.warn("Attempted to upload empty file");
                return ResponseEntity.badRequest().build();
            }
            
            // Validar que sea un archivo CSV
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
                logger.warn("Attempted to upload non-CSV file: {}", filename);
                return ResponseEntity.badRequest().build();
            }
            
            // Validar Content-Type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("text/csv")) {
                logger.warn("Invalid content type: {}", contentType);
                return ResponseEntity.badRequest().build();
            }
            
            logger.info("Processing CSV file: {} ({} bytes)", filename, file.getSize());
            
            // Procesar el archivo CSV
            MigrationReport report = migrationService.processCsv(file);
            
            logger.info("CSV processing completed successfully. Records: {} total, {} success, {} errors",
                    report.getTotalRecords(), report.getSuccessRecords(), report.getErrorRecords());
            
            // Devolver solo código HTTP 200 OK sin body
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid CSV file: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error processing CSV file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
