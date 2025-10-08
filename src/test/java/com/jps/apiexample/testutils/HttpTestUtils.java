package com.jps.apiexample.testutils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Utilidades para hacer requests HTTP en las pruebas
 */
public class HttpTestUtils {
    
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    
    /**
     * Crea un archivo multipart para testing
     */
    public static MultiValueMap<String, Object> createMultipartFile(String fieldName, String filename, String content) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        
        body.add(fieldName, resource);
        return body;
    }
    
    /**
     * Hace un request POST con archivo multipart
     */
    public static ResponseEntity<String> postMultipart(String url, String fieldName, String filename, String content) {
        MultiValueMap<String, Object> body = createMultipartFile(fieldName, filename, content);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        return REST_TEMPLATE.postForEntity(url, requestEntity, String.class);
    }
    
    /**
     * Hace un request GET
     */
    public static ResponseEntity<String> get(String url) {
        return REST_TEMPLATE.getForEntity(url, String.class);
    }
    
    /**
     * Hace un request GET con headers personalizados
     */
    public static ResponseEntity<String> get(String url, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return REST_TEMPLATE.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
    }
    
    /**
     * Hace un request POST
     */
    public static ResponseEntity<String> post(String url, Object body) {
        return REST_TEMPLATE.postForEntity(url, body, String.class);
    }
    
    /**
     * Hace un request POST con headers personalizados
     */
    public static ResponseEntity<String> post(String url, Object body, HttpHeaders headers) {
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return REST_TEMPLATE.exchange(url, org.springframework.http.HttpMethod.POST, entity, String.class);
    }
    
    /**
     * Crea headers básicos
     */
    public static HttpHeaders createBasicHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    /**
     * Crea headers para multipart
     */
    public static HttpHeaders createMultipartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }
}
