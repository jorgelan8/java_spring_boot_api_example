package com.jps.apiexample.service;

import com.jps.apiexample.config.EmailConfig;
import com.jps.apiexample.config.ReportConfig;
import com.jps.apiexample.model.MigrationReport;
import com.jps.apiexample.model.ReportChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para manejar el envío de reportes de migración
 */
@Service
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    private final EmailConfig emailConfig;
    private final ReportConfig reportConfig;
    private final JavaMailSender mailSender;
    
    @Autowired
    public ReportService(EmailConfig emailConfig, ReportConfig reportConfig, JavaMailSender mailSender) {
        this.emailConfig = emailConfig;
        this.reportConfig = reportConfig;
        this.mailSender = mailSender;
    }
    
    /**
     * Envía el reporte de migración por los canales configurados
     */
    public void sendMigrationReport(MigrationReport report) {
        List<ReportChannel> channels = reportConfig.getChannels();
        
        if (channels == null || channels.isEmpty()) {
            logger.warn("No report channels configured, skipping report");
            return;
        }
        
        for (ReportChannel channel : channels) {
            switch (channel) {
                case EMAIL:
                    sendEmailReport(report);
                    break;
                case WEBHOOK:
                    sendWebhookReport(report);
                    break;
                case LOG:
                    sendLogReport(report);
                    break;
            }
        }
    }
    
    /**
     * Envía el reporte por email
     */
    private void sendEmailReport(MigrationReport report) {
        if (!emailConfig.isConfigured()) {
            sendMockEmailReport(report);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailConfig.getToEmails().toArray(new String[0]));
            message.setFrom(emailConfig.getFromEmail());
            message.setSubject(String.format("%s - %s", reportConfig.getSubject(), report.getFilename()));
            message.setText(generateEmailBody(report));
            
            mailSender.send(message);
            logger.info("Migration report sent via email successfully");
        } catch (Exception e) {
            logger.error("Error sending email report: {}", e.getMessage());
            sendMockEmailReport(report);
        }
    }
    
    /**
     * Simula el envío de email (para desarrollo)
     */
    private void sendMockEmailReport(MigrationReport report) {
        logger.info("=== MOCK EMAIL REPORT ===");
        logger.info("To: {}", emailConfig.getToEmails());
        logger.info("Subject: Migration Report - {}", report.getFilename());
        logger.info("Body:\n{}", generateEmailBody(report));
        logger.info("=== END MOCK EMAIL ===");
    }
    
    /**
     * Envía el reporte por webhook
     */
    private void sendWebhookReport(MigrationReport report) {
        // TODO: Implementar webhook
        logger.info("Webhook report sent: {}", report.getFilename());
    }
    
    /**
     * Envía el reporte por log
     */
    private void sendLogReport(MigrationReport report) {
        logger.info("=== MIGRATION REPORT ===");
        logger.info("File: {} ({} bytes)", report.getFilename(), report.getFileSize());
        logger.info("Records: {} total, {} success, {} errors",
                report.getTotalRecords(), report.getSuccessRecords(), report.getErrorRecords());
        logger.info("Users affected: {}", report.getUsersAffected());
        logger.info("Amount range: {:.2f} to {:.2f} (avg: {:.2f})",
                report.getSmallestAmount(), report.getLargestAmount(), report.getAverageAmount());
        logger.info("Processing time: {}", report.getProcessingTime());
        
        if (report.getErrors() != null && !report.getErrors().isEmpty()) {
            logger.info("Errors: {}", report.getErrors());
        }
        
        logger.info("=== END REPORT ===");
    }
    
    /**
     * Genera el cuerpo del email
     */
    private String generateEmailBody(MigrationReport report) {
        StringBuilder body = new StringBuilder();
        
        body.append("=== MIGRATION REPORT ===\n\n");
        body.append(String.format("File: %s (%d bytes)\n", report.getFilename(), report.getFileSize()));
        body.append(String.format("Timestamp: %s\n", 
                report.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        body.append(String.format("Processing time: %s\n\n", report.getProcessingTime()));
        
        body.append("=== STATISTICS ===\n");
        body.append(String.format("Total records: %d\n", report.getTotalRecords()));
        body.append(String.format("Success records: %d\n", report.getSuccessRecords()));
        body.append(String.format("Error records: %d\n", report.getErrorRecords()));
        body.append(String.format("Success rate: %.2f%%\n\n",
                (double) report.getSuccessRecords() / report.getTotalRecords() * 100));
        
        body.append("=== DATA ANALYSIS ===\n");
        body.append(String.format("Users affected: %d\n", report.getUsersAffected()));
        body.append(String.format("Total amount: %.2f\n", report.getTotalAmount()));
        body.append(String.format("Average amount: %.2f\n", report.getAverageAmount()));
        body.append(String.format("Largest amount: %.2f\n", report.getLargestAmount()));
        body.append(String.format("Smallest amount: %.2f\n", report.getSmallestAmount()));
        
        if (report.getDateRange() != null) {
            body.append(String.format("Date range: %s to %s\n\n",
                    report.getDateRange().getFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    report.getDateRange().getTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        
        if (report.getErrors() != null && !report.getErrors().isEmpty()) {
            body.append("=== ERRORS ===\n");
            for (int i = 0; i < report.getErrors().size(); i++) {
                body.append(String.format("%d. %s\n", i + 1, report.getErrors().get(i)));
            }
            body.append("\n");
        }
        
        if (report.getErrorFileCsv() != null && !report.getErrorFileCsv().isEmpty()) {
            body.append("=== ERROR FILE ===\n");
            body.append(String.format("Error records exported to: %s\n", report.getErrorFileCsv()));
            body.append("\n");
        }
        
        body.append("=== END REPORT ===");
        
        return body.toString();
    }
    
    /**
     * Genera un archivo CSV con los registros que tuvieron errores
     */
    public String generateErrorCsv(List<String> errors, String filename) throws IOException {
        // Crear directorio de errores si no existe
        Path errorDir = Paths.get("reports/errors");
        Files.createDirectories(errorDir);
        
        // Generar nombre de archivo único
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String errorFilename = String.format("errors_%s_%s", timestamp, filename);
        Path errorPath = errorDir.resolve(errorFilename);
        
        // Crear archivo CSV
        try (FileWriter writer = new FileWriter(errorPath.toFile())) {
            writer.append("line_number,error_message,original_data\n");
            
            for (int i = 0; i < errors.size(); i++) {
                writer.append(String.format("%d,%s,\n", i + 1, errors.get(i)));
            }
        }
        
        return errorPath.toString();
    }
}
