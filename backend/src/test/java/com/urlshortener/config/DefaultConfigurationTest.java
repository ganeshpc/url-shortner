package com.urlshortener.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultConfigurationTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ShortCodeProperties shortCodeProperties;

    @Autowired
    private ValidatedAppProperties validatedAppProperties;

    @Test
    void testDefaultAppPropertiesValues() {
        // Test values from application.properties
        assertEquals("http://localhost:8080", appProperties.getBaseUrl());
        assertEquals("http://localhost:3000", appProperties.getFrontendUrl());
        assertNotNull(appProperties.getCors());
        assertEquals("http://localhost:3000", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    void testDefaultShortCodePropertiesValues() {
        // Test default values from ShortCodeProperties class
        assertEquals(7, shortCodeProperties.getLength());
        assertEquals(5, shortCodeProperties.getMaxRetryAttempts());
        assertEquals(ShortCodeProperties.GenerationStrategy.MIXED, shortCodeProperties.getStrategy());
        assertEquals(ShortCodeProperties.CharacterSet.BASE58, shortCodeProperties.getCharacterSet());
    }

    @Test
    void testDefaultValidatedAppPropertiesValues() {
        // Test values from application.properties loaded into ValidatedAppProperties
        assertEquals("http://localhost:8080", validatedAppProperties.getBaseUrl());
        assertEquals("http://localhost:3000", validatedAppProperties.getFrontendUrl());
        assertNotNull(validatedAppProperties.getCors());
        assertEquals("http://localhost:3000", validatedAppProperties.getCors().getAllowedOrigins());
    }

    @Test
    void testAllConfigurationsLoaded() {
        // Ensure all configuration beans are properly loaded
        assertNotNull(appProperties);
        assertNotNull(shortCodeProperties);
        assertNotNull(validatedAppProperties);
    }

    @Test
    void testCorsConfigurationDefaults() {
        // Test that CORS configuration has proper defaults
        assertNotNull(appProperties.getCors());
        assertNotNull(validatedAppProperties.getCors());
        assertEquals(appProperties.getCors().getAllowedOrigins(),
                    validatedAppProperties.getCors().getAllowedOrigins());
    }
}