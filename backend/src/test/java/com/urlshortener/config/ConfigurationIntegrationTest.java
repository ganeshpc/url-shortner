package com.urlshortener.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.base-url=https://test-api.example.com",
    "app.frontend-url=https://test-frontend.example.com",
    "app.cors.allowed-origins=https://test-frontend.example.com,https://admin.example.com",
    "app.shortcode.length=8",
    "app.shortcode.max-retry-attempts=6",
    "app.shortcode.strategy=HASH_BASED",
    "app.shortcode.character-set=BASE62"
})
class ConfigurationIntegrationTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ShortCodeProperties shortCodeProperties;

    @Autowired
    private ValidatedAppProperties validatedAppProperties;

    @Test
    void testAppPropertiesBinding() {
        // Test traditional AppProperties binding
        assertEquals("https://test-api.example.com", appProperties.getBaseUrl());
        assertEquals("https://test-frontend.example.com", appProperties.getFrontendUrl());
        assertNotNull(appProperties.getCors());
        assertEquals("https://test-frontend.example.com,https://admin.example.com",
                    appProperties.getCors().getAllowedOrigins());
    }

    @Test
    void testShortCodePropertiesBinding() {
        // Test ShortCodeProperties binding
        assertEquals(8, shortCodeProperties.getLength());
        assertEquals(6, shortCodeProperties.getMaxRetryAttempts());
        assertEquals(ShortCodeProperties.GenerationStrategy.HASH_BASED, shortCodeProperties.getStrategy());
        assertEquals(ShortCodeProperties.CharacterSet.BASE62, shortCodeProperties.getCharacterSet());
    }

    @Test
    void testValidatedAppPropertiesBinding() {
        // Test ValidatedAppProperties binding
        assertEquals("https://test-api.example.com", validatedAppProperties.getBaseUrl());
        assertEquals("https://test-frontend.example.com", validatedAppProperties.getFrontendUrl());
        assertNotNull(validatedAppProperties.getCors());
        assertEquals("https://test-frontend.example.com,https://admin.example.com",
                    validatedAppProperties.getCors().getAllowedOrigins());
    }

    @Test
    void testConfigurationConsistency() {
        // Ensure all configuration classes have consistent values
        assertEquals(appProperties.getBaseUrl(), validatedAppProperties.getBaseUrl());
        assertEquals(appProperties.getFrontendUrl(), validatedAppProperties.getFrontendUrl());
        assertEquals(appProperties.getCors().getAllowedOrigins(),
                    validatedAppProperties.getCors().getAllowedOrigins());
    }
}