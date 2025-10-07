package com.jps.apiexample.model;

/**
 * Representa los canales de notificación para reportes
 */
public enum ReportChannel {
    EMAIL("email"),
    WEBHOOK("webhook"),
    LOG("log");
    
    private final String value;
    
    ReportChannel(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * Convierte un string a ReportChannel
     */
    public static ReportChannel fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (ReportChannel channel : ReportChannel.values()) {
            if (channel.value.equalsIgnoreCase(value.trim())) {
                return channel;
            }
        }
        
        throw new IllegalArgumentException("Invalid report channel: " + value);
    }
}
