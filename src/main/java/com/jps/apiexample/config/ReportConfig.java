package com.jps.apiexample.config;

import com.jps.apiexample.model.ReportChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de reportes
 */
@Configuration
@ConfigurationProperties(prefix = "report")
public class ReportConfig {
    
    private List<ReportChannel> channels;
    private String subject = "Migration Report - API Stori";
    
    // Getters and Setters
    public List<ReportChannel> getChannels() {
        return channels;
    }
    
    public void setChannels(List<ReportChannel> channels) {
        this.channels = channels;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    @Override
    public String toString() {
        return "ReportConfig{" +
                "channels=" + channels +
                ", subject='" + subject + '\'' +
                '}';
    }
}
