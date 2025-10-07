package com.jps.apiexample.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

/**
 * Representa el reporte de migración
 */
public class MigrationReport {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String filename;
    
    @JsonProperty("file_size")
    private Long fileSize;
    
    @JsonProperty("total_records")
    private Integer totalRecords;
    
    @JsonProperty("success_records")
    private Integer successRecords;
    
    @JsonProperty("error_records")
    private Integer errorRecords;
    
    @JsonProperty("processing_time")
    private Duration processingTime;
    
    @JsonProperty("users_affected")
    private Integer usersAffected;
    
    @JsonProperty("total_amount")
    private Double totalAmount;
    
    @JsonProperty("average_amount")
    private Double averageAmount;
    
    @JsonProperty("largest_amount")
    private Double largestAmount;
    
    @JsonProperty("smallest_amount")
    private Double smallestAmount;
    
    @JsonProperty("date_range")
    private DateRange dateRange;
    
    private List<String> errors;
    
    @JsonProperty("error_file_csv")
    private String errorFileCsv;
    
    // Constructors
    public MigrationReport() {}
    
    public MigrationReport(LocalDateTime timestamp, String filename, Long fileSize,
                         Integer totalRecords, Integer successRecords, Integer errorRecords,
                         Duration processingTime, Integer usersAffected, Double totalAmount,
                         Double averageAmount, Double largestAmount, Double smallestAmount,
                         DateRange dateRange, List<String> errors, String errorFileCsv) {
        this.timestamp = timestamp;
        this.filename = filename;
        this.fileSize = fileSize;
        this.totalRecords = totalRecords;
        this.successRecords = successRecords;
        this.errorRecords = errorRecords;
        this.processingTime = processingTime;
        this.usersAffected = usersAffected;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.largestAmount = largestAmount;
        this.smallestAmount = smallestAmount;
        this.dateRange = dateRange;
        this.errors = errors;
        this.errorFileCsv = errorFileCsv;
    }
    
    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Integer getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public Integer getSuccessRecords() {
        return successRecords;
    }
    
    public void setSuccessRecords(Integer successRecords) {
        this.successRecords = successRecords;
    }
    
    public Integer getErrorRecords() {
        return errorRecords;
    }
    
    public void setErrorRecords(Integer errorRecords) {
        this.errorRecords = errorRecords;
    }
    
    public Duration getProcessingTime() {
        return processingTime;
    }
    
    public void setProcessingTime(Duration processingTime) {
        this.processingTime = processingTime;
    }
    
    public Integer getUsersAffected() {
        return usersAffected;
    }
    
    public void setUsersAffected(Integer usersAffected) {
        this.usersAffected = usersAffected;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Double getAverageAmount() {
        return averageAmount;
    }
    
    public void setAverageAmount(Double averageAmount) {
        this.averageAmount = averageAmount;
    }
    
    public Double getLargestAmount() {
        return largestAmount;
    }
    
    public void setLargestAmount(Double largestAmount) {
        this.largestAmount = largestAmount;
    }
    
    public Double getSmallestAmount() {
        return smallestAmount;
    }
    
    public void setSmallestAmount(Double smallestAmount) {
        this.smallestAmount = smallestAmount;
    }
    
    public DateRange getDateRange() {
        return dateRange;
    }
    
    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public String getErrorFileCsv() {
        return errorFileCsv;
    }
    
    public void setErrorFileCsv(String errorFileCsv) {
        this.errorFileCsv = errorFileCsv;
    }
    
    // Inner class for date range
    public static class DateRange {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime from;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime to;
        
        public DateRange() {}
        
        public DateRange(LocalDateTime from, LocalDateTime to) {
            this.from = from;
            this.to = to;
        }
        
        public LocalDateTime getFrom() {
            return from;
        }
        
        public void setFrom(LocalDateTime from) {
            this.from = from;
        }
        
        public LocalDateTime getTo() {
            return to;
        }
        
        public void setTo(LocalDateTime to) {
            this.to = to;
        }
        
        @Override
        public String toString() {
            return "DateRange{" +
                    "from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
    
    @Override
    public String toString() {
        return "MigrationReport{" +
                "timestamp=" + timestamp +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                ", totalRecords=" + totalRecords +
                ", successRecords=" + successRecords +
                ", errorRecords=" + errorRecords +
                ", processingTime=" + processingTime +
                ", usersAffected=" + usersAffected +
                ", totalAmount=" + totalAmount +
                ", averageAmount=" + averageAmount +
                ", largestAmount=" + largestAmount +
                ", smallestAmount=" + smallestAmount +
                ", dateRange=" + dateRange +
                ", errors=" + errors +
                ", errorFileCsv='" + errorFileCsv + '\'' +
                '}';
    }
}
