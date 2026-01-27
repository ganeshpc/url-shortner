package com.urlshortener.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AppPropertiesTest {

    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
    }

    @Test
    @DisplayName("Should have default constructor and initialize cors")
    void testDefaultConstructor() {
        // Given - appProperties is created in setUp

        // Then
        assertNotNull(appProperties);
        assertNotNull(appProperties.getCors()); // Should be initialized
        assertNull(appProperties.getBaseUrl());
        assertNull(appProperties.getFrontendUrl());
    }

    @Test
    @DisplayName("Should set and get baseUrl")
    void testBaseUrl() {
        // Given
        String baseUrl = "http://localhost:8080";

        // When
        appProperties.setBaseUrl(baseUrl);

        // Then
        assertEquals(baseUrl, appProperties.getBaseUrl());
    }

    @Test
    @DisplayName("Should set and get frontendUrl")
    void testFrontendUrl() {
        // Given
        String frontendUrl = "http://localhost:3000";

        // When
        appProperties.setFrontendUrl(frontendUrl);

        // Then
        assertEquals(frontendUrl, appProperties.getFrontendUrl());
    }

    @Test
    @DisplayName("Should set and get cors configuration")
    void testCorsConfiguration() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins("http://localhost:3000");

        // When
        appProperties.setCors(cors);

        // Then
        assertEquals(cors, appProperties.getCors());
        assertEquals("http://localhost:3000", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should initialize cors with default instance")
    void testCorsDefaultInitialization() {
        // Then
        assertNotNull(appProperties.getCors());
        assertNull(appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should test Cors nested class getters and setters")
    void testCorsNestedClass() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        String allowedOrigins = "http://localhost:3000,http://localhost:3001";

        // When
        cors.setAllowedOrigins(allowedOrigins);

        // Then
        assertEquals(allowedOrigins, cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle null allowedOrigins in Cors")
    void testCorsNullAllowedOrigins() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();

        // When
        cors.setAllowedOrigins(null);

        // Then
        assertNull(cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle empty allowedOrigins in Cors")
    void testCorsEmptyAllowedOrigins() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();

        // When
        cors.setAllowedOrigins("");

        // Then
        assertEquals("", cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle multiple origins in Cors")
    void testCorsMultipleOrigins() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        String multipleOrigins = "http://localhost:3000,https://example.com,http://test.com:8080";

        // When
        cors.setAllowedOrigins(multipleOrigins);

        // Then
        assertEquals(multipleOrigins, cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle wildcard origins in Cors")
    void testCorsWildcardOrigins() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        String wildcardOrigins = "*";

        // When
        cors.setAllowedOrigins(wildcardOrigins);

        // Then
        assertEquals(wildcardOrigins, cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle origins with special characters")
    void testCorsSpecialCharacters() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        String specialOrigins = "https://sub.domain.com:8443,http://localhost:3000/path";

        // When
        cors.setAllowedOrigins(specialOrigins);

        // Then
        assertEquals(specialOrigins, cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should overwrite previous allowedOrigins value")
    void testCorsOverwriteValue() {
        // Given
        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins("first");

        // When
        cors.setAllowedOrigins("second");

        // Then
        assertEquals("second", cors.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // When
        appProperties.setBaseUrl(null);
        appProperties.setFrontendUrl(null);
        appProperties.setCors(null);

        // Then
        assertNull(appProperties.getBaseUrl());
        assertNull(appProperties.getFrontendUrl());
        assertNull(appProperties.getCors());
    }

    @Test
    @DisplayName("Should have ConfigurationProperties annotation")
    void testConfigurationPropertiesAnnotation() {
        // Test that the class has the correct annotation
        assertTrue(AppProperties.class.isAnnotationPresent(org.springframework.boot.context.properties.ConfigurationProperties.class));
        assertTrue(AppProperties.class.isAnnotationPresent(org.springframework.stereotype.Component.class));

        var annotation = AppProperties.class.getAnnotation(org.springframework.boot.context.properties.ConfigurationProperties.class);
        assertEquals("app", annotation.prefix());
    }

    @Test
    @DisplayName("Should test complete configuration setup")
    void testCompleteConfiguration() {
        // Given
        String baseUrl = "https://api.urlshortener.com";
        String frontendUrl = "https://urlshortener.com";
        String allowedOrigins = "https://urlshortener.com,https://admin.urlshortener.com";

        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins(allowedOrigins);

        // When
        appProperties.setBaseUrl(baseUrl);
        appProperties.setFrontendUrl(frontendUrl);
        appProperties.setCors(cors);

        // Then
        assertEquals(baseUrl, appProperties.getBaseUrl());
        assertEquals(frontendUrl, appProperties.getFrontendUrl());
        assertEquals(allowedOrigins, appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should create new Cors instance")
    void testCorsInstanceCreation() {
        // When
        AppProperties.Cors cors1 = new AppProperties.Cors();
        AppProperties.Cors cors2 = new AppProperties.Cors();

        // Then
        assertNotNull(cors1);
        assertNotNull(cors2);
        assertNotSame(cors1, cors2); // Different instances
    }

    @Test
    @DisplayName("Should test field initialization in constructor")
    void testFieldInitialization() {
        // Given - new instance created in setUp

        // Then - verify the cors field is initialized
        assertNotNull(appProperties.getCors());
        assertNull(appProperties.getCors().getAllowedOrigins());

        // Verify other fields are null by default
        assertNull(appProperties.getBaseUrl());
        assertNull(appProperties.getFrontendUrl());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStringValues() {
        // When
        appProperties.setBaseUrl("");
        appProperties.setFrontendUrl("");

        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins("");
        appProperties.setCors(cors);

        // Then
        assertEquals("", appProperties.getBaseUrl());
        assertEquals("", appProperties.getFrontendUrl());
        assertEquals("", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle whitespace strings")
    void testWhitespaceStringValues() {
        // When
        appProperties.setBaseUrl("   ");
        appProperties.setFrontendUrl("   ");

        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins("   ");
        appProperties.setCors(cors);

        // Then
        assertEquals("   ", appProperties.getBaseUrl());
        assertEquals("   ", appProperties.getFrontendUrl());
        assertEquals("   ", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should replace existing cors configuration")
    void testCorsReplacement() {
        // Given - initial cors configuration
        AppProperties.Cors initialCors = appProperties.getCors();
        initialCors.setAllowedOrigins("initial");

        // When - replace with new cors configuration
        AppProperties.Cors newCors = new AppProperties.Cors();
        newCors.setAllowedOrigins("new");
        appProperties.setCors(newCors);

        // Then
        assertNotSame(initialCors, appProperties.getCors());
        assertEquals("new", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle multiple property updates")
    void testMultiplePropertyUpdates() {
        // When - update properties multiple times
        appProperties.setBaseUrl("first");
        appProperties.setBaseUrl("second");
        appProperties.setFrontendUrl("first");
        appProperties.setFrontendUrl("second");

        AppProperties.Cors cors1 = new AppProperties.Cors();
        cors1.setAllowedOrigins("first");
        appProperties.setCors(cors1);

        AppProperties.Cors cors2 = new AppProperties.Cors();
        cors2.setAllowedOrigins("second");
        appProperties.setCors(cors2);

        // Then - only the last values should be retained
        assertEquals("second", appProperties.getBaseUrl());
        assertEquals("second", appProperties.getFrontendUrl());
        assertEquals("second", appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle special characters in URLs")
    void testSpecialCharactersInUrls() {
        // When
        String specialBaseUrl = "https://api.test.com/path?param=value&other=test";
        String specialFrontendUrl = "https://frontend.test.com:8080/path#section";
        String specialOrigins = "https://origin1.com, https://origin2.com:443";

        appProperties.setBaseUrl(specialBaseUrl);
        appProperties.setFrontendUrl(specialFrontendUrl);

        AppProperties.Cors cors = new AppProperties.Cors();
        cors.setAllowedOrigins(specialOrigins);
        appProperties.setCors(cors);

        // Then
        assertEquals(specialBaseUrl, appProperties.getBaseUrl());
        assertEquals(specialFrontendUrl, appProperties.getFrontendUrl());
        assertEquals(specialOrigins, appProperties.getCors().getAllowedOrigins());
    }

    @Test
    @DisplayName("Should test component annotation presence")
    void testComponentAnnotation() {
        // Test that the class has the Component annotation
        assertTrue(AppProperties.class.isAnnotationPresent(org.springframework.stereotype.Component.class));
    }

    @Test
    @DisplayName("Should verify annotation values")
    void testAnnotationValues() {
        // Test ConfigurationProperties annotation
        var configPropsAnnotation = AppProperties.class.getAnnotation(
            org.springframework.boot.context.properties.ConfigurationProperties.class);
        assertEquals("app", configPropsAnnotation.prefix());

        // Test Component annotation (should exist but no specific value to check)
        assertTrue(AppProperties.class.isAnnotationPresent(org.springframework.stereotype.Component.class));
    }
}