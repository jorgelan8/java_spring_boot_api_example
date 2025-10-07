package com.jps.apiexample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de email
 */
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailConfig {
    
    private String smtpHost = "smtp.gmail.com";
    private Integer smtpPort = 587;
    private String username;
    private String password;
    private String fromEmail;
    private List<String> toEmails;
    
    // Getters and Setters
    public String getSmtpHost() {
        return smtpHost;
    }
    
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }
    
    public Integer getSmtpPort() {
        return smtpPort;
    }
    
    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFromEmail() {
        return fromEmail;
    }
    
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }
    
    public List<String> getToEmails() {
        return toEmails;
    }
    
    public void setToEmails(List<String> toEmails) {
        this.toEmails = toEmails;
    }
    
    /**
     * Verifica si la configuración de email está completa
     */
    public boolean isConfigured() {
        return smtpHost != null && !smtpHost.isEmpty() &&
               username != null && !username.isEmpty() &&
               password != null && !password.isEmpty() &&
               fromEmail != null && !fromEmail.isEmpty() &&
               toEmails != null && !toEmails.isEmpty();
    }
    
    @Override
    public String toString() {
        return "EmailConfig{" +
                "smtpHost='" + smtpHost + '\'' +
                ", smtpPort=" + smtpPort +
                ", username='" + username + '\'' +
                ", fromEmail='" + fromEmail + '\'' +
                ", toEmails=" + toEmails +
                '}';
    }
}
