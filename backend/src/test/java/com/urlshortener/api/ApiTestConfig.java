package com.urlshortener.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for API tests.
 * Provides beans specific to testing needs.
 */
@TestConfiguration
public class ApiTestConfig {

    /**
     * Configure ObjectMapper for API tests with Java 8 time module.
     */
    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for LocalDateTime serialization
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}