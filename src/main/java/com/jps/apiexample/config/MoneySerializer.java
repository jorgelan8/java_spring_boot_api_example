package com.jps.apiexample.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Serializador personalizado para valores monetarios que siempre muestra 2 decimales
 */
public class MoneySerializer extends JsonSerializer<Double> {
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    
    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Escribir como número en lugar de string
            gen.writeNumber(value);
        }
    }
}
