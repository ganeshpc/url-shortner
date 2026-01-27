package com.urlshortener.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    @DisplayName("Should create AppConfig with all parameters")
    void testAppConfigCreation() {
        // Given
        String baseUrl = "http://localhost:8080";
        String frontendUrl = "http://localhost:3000";
        AppConfig.Cors cors = new AppConfig.Cors("http://localhost:3000");

        // When
        AppConfig config = new AppConfig(baseUrl, frontendUrl, cors);

        // Then
        assertEquals(baseUrl, config.baseUrl());
        assertEquals(frontendUrl, config.frontendUrl());
        assertEquals(cors, config.cors());
    }

    @Test
    @DisplayName("Should create AppConfig with null values")
    void testAppConfigWithNullValues() {
        // When
        AppConfig config = new AppConfig(null, null, null);

        // Then
        assertNull(config.baseUrl());
        assertNull(config.frontendUrl());
        assertNull(config.cors());
    }

    @Test
    @DisplayName("Should create Cors record with allowed origins")
    void testCorsRecord() {
        // Given
        String allowedOrigins = "http://localhost:3000,http://localhost:3001";

        // When
        AppConfig.Cors cors = new AppConfig.Cors(allowedOrigins);

        // Then
        assertEquals(allowedOrigins, cors.allowedOrigins());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        // Given
        AppConfig.Cors cors1 = new AppConfig.Cors("http://localhost:3000");
        AppConfig.Cors cors2 = new AppConfig.Cors("http://localhost:3000");
        AppConfig.Cors cors3 = new AppConfig.Cors("http://localhost:3001");

        AppConfig config1 = new AppConfig("http://localhost:8080", "http://localhost:3000", cors1);
        AppConfig config2 = new AppConfig("http://localhost:8080", "http://localhost:3000", cors2);
        AppConfig config3 = new AppConfig("http://localhost:8081", "http://localhost:3000", cors1);

        // Then
        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertNotEquals(config1, null);
        assertNotEquals(config1, "not a config");

        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    @DisplayName("Should generate toString representation")
    void testToString() {
        // Given
        AppConfig.Cors cors = new AppConfig.Cors("http://localhost:3000");
        AppConfig config = new AppConfig("http://localhost:8080", "http://localhost:3000", cors);

        // When
        String toString = config.toString();

        // Then
        assertTrue(toString.contains("AppConfig"));
        assertTrue(toString.contains("http://localhost:8080"));
        assertTrue(toString.contains("http://localhost:3000"));
        assertTrue(toString.contains("Cors"));
    }

    @Test
    @DisplayName("Should have ConfigurationProperties annotation")
    void testConfigurationPropertiesAnnotation() {
        // Test that the class has the correct annotation
        assertTrue(AppConfig.class.isAnnotationPresent(org.springframework.boot.context.properties.ConfigurationProperties.class));

        var annotation = AppConfig.class.getAnnotation(org.springframework.boot.context.properties.ConfigurationProperties.class);
        assertEquals("app", annotation.prefix());
    }
}